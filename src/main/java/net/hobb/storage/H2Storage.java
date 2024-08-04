package net.hobb.storage;


import net.hobb.HobbUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/**
 * This class is responsible for storing data in an H2 database
 * Because we use the {@link TypedKeyValue} class, we can store any type of data if we just have unique correct keys
 */
public class H2Storage extends Storage {
  private static final String JDBC_URL = "jdbc:h2:./data/database"; // Change the path as needed
  private static final String USER = "sa";
  private static final String PASSWORD = "password";
  private Connection connection;


  /**
   * This constructor creates a new H2Storage object by initializing the database
   */
  public H2Storage() {
    try {
      if(HobbUtils.isNotHooked()) throw new IllegalStateException("H2Storage is not hooked!");
      Class.forName("org.h2.Driver");
      connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
      String couldInit = init("key_value").get() ? "Successfully initialized" : "Could not Initialize";
      HobbUtils.getHookedPlugin().getLogger().info(couldInit + " H2 storage!");
    } catch (ClassNotFoundException | SQLException | IllegalStateException | InterruptedException e) {
      HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE,"Initializing the H2 Database failed!", e);
    } catch(ExecutionException e) {
      HobbUtils.getThisPlugin().getLogger().log(Level.SEVERE,"Initializing the H2 Database failed!", e.getCause());
    }
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
  public CompletableFuture<Boolean> setValue(@NotNull TypedKeyValue<?> tkv, @NotNull Object value) {
    return CompletableFuture.supplyAsync(() -> {
      String insertSQL = "MERGE INTO `key_value` (`key`, `value`) VALUES (?, ?);";
      try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
        pstmt.setString(1, tkv.getKey());
        pstmt.setObject(2, value);
        return pstmt.executeUpdate() != 0;
      } catch (SQLException e) {
        HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE, "Could not set value!", e);
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
        return rs.next() ? tkv.getType().cast(rs.getObject("value", Object.class)) : null;
      } catch (SQLException e) {
        HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE, "Could not get value!", e);
      }
      return null;
    });
  }
  @Override
  public CompletableFuture<Boolean> removeValue(@NotNull TypedKeyValue<?> tkv) {
    return CompletableFuture.supplyAsync(() -> {
      String deleteSQL = "DELETE FROM `key_value` WHERE `key` = ?;";
      try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
        pstmt.setString(1, tkv.getKey());
        return pstmt.executeUpdate() != 0;
      } catch (SQLException e) {
        HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE, "Could not remove value!", e);
        return false;
      }
    });
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
        HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE, "Could not clear table!", e);
        return false;
      }
    });
  }
  /**
   * This method creates a new kav_value Table using prepared statements
   * @param tableName The name of the table to create
   * @return Whether the table was created successfully
   */
  public CompletableFuture<Boolean> createTable(String tableName) {
    return CompletableFuture.supplyAsync(() -> {
      String createSQL = "CREATE TABLE IF NOT EXISTS `?` (`key` VARCHAR(255) NOT NULL, `value` JAVA_OBJECT NOT NULL);";
      try(PreparedStatement pstmt = connection.prepareStatement(createSQL)) {
        pstmt.setString(1, tableName);
        return pstmt.execute();
      } catch(SQLException e) {
        HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE, "Could not create table `" + tableName + "`", e);
        return false;
      }
    });
  }




}
