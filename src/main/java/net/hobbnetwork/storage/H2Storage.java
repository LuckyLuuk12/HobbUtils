package net.hobbnetwork.storage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.hobbnetwork.managers.HookManager;
import net.hobbnetwork.utils.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class H2Storage extends Storage {
  private final HookManager hookManager;
  private static final String USER = "sa";
  private static final String PASSWORD = "password";
  private Connection connection;
  private final Gson gson = new Gson();

  public H2Storage(HookManager hookManager) {
    this.hookManager = hookManager;
    try {
      String jdbcUrl = getH2URL(hookManager);
      Class.forName("org.h2.Driver");
      connection = DriverManager.getConnection(jdbcUrl, USER, PASSWORD);
      String couldInit = init("hobb-storage").get() ? "Successfully initialized" : "Could not Initialize";
      hookManager.log(LogUtil.LogLevel.DEBUG, "[H2Storage] " + couldInit + " H2 storage!");
    } catch (ClassNotFoundException | SQLException | IllegalStateException | InterruptedException | ExecutionException e) {
      hookManager.log(LogUtil.LogLevel.CRASH, "[H2Storage] Initializing the H2 Database failed!", e);
    }
  }

  private static @NotNull String getH2URL(HookManager hookManager) {
    if (!hookManager.isHooked()) throw new IllegalStateException("H2Storage is not hooked!");
    File databaseDir = new File(hookManager.getPlugin().getDataFolder(), "database");
    if (!databaseDir.exists() && !databaseDir.mkdirs()) {
      throw new IllegalStateException("Failed to create database directory: " + databaseDir.getPath());
    }
    return "jdbc:h2:" + databaseDir.getAbsolutePath() + "/database";
  }

  @Override
  public CompletableFuture<Boolean> init(@NotNull String tableName, String... useless) {
    return createTable(tableName);
  }

  @Override
  public CompletableFuture<Boolean> setValue(@NotNull TypedKeyValue<?> tkv, @Nullable Object value) {
    return CompletableFuture.supplyAsync(() -> {
      String insertSQL = value == null
        ? "DELETE FROM `key_value` WHERE `key` = ?;"
        : "MERGE INTO `key_value` (`key`, `value`) VALUES (?, ?);";
      try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
        pstmt.setString(1, tkv.getKey());
        if (value != null) pstmt.setString(2, gson.toJson(value)); // Convert value to JSON
        return pstmt.executeUpdate() != 0;
      } catch (SQLException e) {
        hookManager.log(Level.SEVERE, "[H2Storage] Could not set value!", e);
        return false;
      }
    });
  }

  @Override
  public CompletableFuture<Object> getValue(@NotNull TypedKeyValue<?> tkv) {
    return CompletableFuture.supplyAsync(() -> {
      String selectSQL = "SELECT `value` FROM `key_value` WHERE `key` = ?;";
      try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
        pstmt.setString(1, tkv.getKey());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) return gson.fromJson(rs.getString("value"), tkv.getType()); // Convert JSON to value
      } catch (SQLException | JsonSyntaxException e) {
        hookManager.log(Level.SEVERE, "[H2Storage] Could not get value!", e);
      }
      return null;
    });
  }

  @Override
  public CompletableFuture<Boolean> removeValue(@NotNull TypedKeyValue<?> tkv) {
    return setValue(tkv, null);
  }

  public CompletableFuture<Boolean> clear() {
    return CompletableFuture.supplyAsync(() -> {
      String deleteSQL = "TRUNCATE TABLE `key_value`;";
      try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
        return pstmt.executeUpdate() != 0;
      } catch (SQLException e) {
        hookManager.log(Level.SEVERE, "[H2Storage] Could not clear table!", e);
        return false;
      }
    });
  }

  public CompletableFuture<Boolean> createTable(String tableName) {
    return CompletableFuture.supplyAsync(() -> {
      String createSQL = "CREATE TABLE IF NOT EXISTS `key_value` (`key` VARCHAR(255) NOT NULL PRIMARY KEY, `value` CLOB NOT NULL);";
      try(Statement stmt = connection.createStatement()) {
        boolean suc = !stmt.execute(createSQL);
        int rows = stmt.getUpdateCount();
        if(rows == 1) hookManager.log(Level.FINE, "[H2Storage] Creating table `key_value`");
        if(rows == 0) hookManager.log(Level.FINEST, "[H2Storage] `key_value` table was found!");
        return suc;
      } catch(SQLException e) {
        hookManager.log(LogUtil.LogLevel.CRASH, "[H2Storage] Could not create table `" + tableName + "` "+e.getMessage());
        return false;
      }
    });
  }
}