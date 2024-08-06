package net.hobbnetwork.storage;


import net.hobbnetwork.managers.HookManager;
import net.hobbnetwork.utils.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/**
 * This class is responsible for storing data in an H2 database
 * Because we use the {@link TypedKeyValue} class, we can store any type of data if we just have unique correct keys
 */
public class H2Storage extends Storage {
  private final HookManager hookManager;
  private static final String USER = "sa";
  private static final String PASSWORD = "password";
  private Connection connection;


  /**
   * This constructor creates a new H2Storage object by initializing the database
   */
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
    // Ensure the directory exists
    File databaseDir = new File(hookManager.getPlugin().getDataFolder(), "database");
    if (!databaseDir.exists() && !databaseDir.mkdirs()) {
      throw new IllegalStateException("Failed to create database directory: " + databaseDir.getPath());
    }
    // Initialize the connection
    return "jdbc:h2:" + databaseDir.getAbsolutePath() + "/database";
  }
  /**
   * This method initializes the key-value table
   * @return Whether the table was initialized successfully
   */
  @Override
  public CompletableFuture<Boolean> init(@NotNull String tableName, String... useless) {
    return createTable(tableName);
  }
  /**
   * This method sets the value of a key
   * @param tkv The key-value pair to set the value of
   * @param value The value to set
   * @return True if the value was set, false otherwise
   */
  @Override
  public CompletableFuture<Boolean> setValue(@NotNull TypedKeyValue<?> tkv, @Nullable Object value) {
    return CompletableFuture.supplyAsync(() -> {
      String insertSQL = value == null
        ? "DELETE FROM `key_value` WHERE `key` = ?;"
        : "MERGE INTO `key_value` (`key`, `value`) VALUES (?, ?);";
      try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
        pstmt.setString(1, tkv.getKey());
        if (value != null) pstmt.setBytes(2, serialize(value)); // Convert value to bytes
        return pstmt.executeUpdate() != 0;
      } catch (SQLException e) {
        hookManager.log(Level.SEVERE, "[H2Storage] Could not set value!", e);
        return false;
      }
    });
  }
  /**
   * This method gets the value of a key
   * @param tkv The key-value pair to get the value of
   * @return The value of the key or null if it does not exist
   */
  @Override
  public CompletableFuture<Object> getValue(@NotNull TypedKeyValue<?> tkv) {
    return CompletableFuture.supplyAsync(() -> {
      String selectSQL = "SELECT `value` FROM `key_value` WHERE `key` = ?;";
      try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
        pstmt.setString(1, tkv.getKey());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) return tkv.getType().cast(deserialize(rs.getBytes("value"))); // Convert bytes to value
      } catch (Exception e) {
        hookManager.log(Level.SEVERE, "[H2Storage] Could not get value!", e);
      }
      return null;
    });
  }
  /**
   * This method removes the key-value pair from the table,
   * using {@link #setValue(TypedKeyValue, Object)} with a null value internally
   * @param tkv The key-value pair to remove
   * @return True if the value was removed, false otherwise
   */
  @Override
  public CompletableFuture<Boolean> removeValue(@NotNull TypedKeyValue<?> tkv) {
    return setValue(tkv, null);
  }


  /**
   * This method clears the table
   * @return True if there were rows to delete and they were deleted, false otherwise
   */
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
  /**
   * This method creates a new kav_value Table using prepared statements
   * @param tableName The name of the table to create
   * @return True if the table was created successfully or if it already exists, false otherwise
   */
  // TODO: check if we shouldn't use JAVA_OBJECT instead of blob and change the (de)serialization code
  public CompletableFuture<Boolean> createTable(String tableName) {
    return CompletableFuture.supplyAsync(() -> {
      String createSQL = "CREATE TABLE IF NOT EXISTS `key_value` (`key` VARCHAR(255) NOT NULL PRIMARY KEY, `value` BLOB NOT NULL);";
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


  private byte[] serialize(Object obj) throws SQLException {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(bos)) {
      oos.writeObject(obj);
      return bos.toByteArray();
    } catch (IOException e) {
      throw new SQLException("Serialization error", e);
    }
  }
  private Object deserialize(byte[] bytes) throws SQLException {
    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
         ObjectInputStream ois = new ObjectInputStream(bis)) {
      return ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new SQLException("Deserialization error", e);
    }
  }


}
