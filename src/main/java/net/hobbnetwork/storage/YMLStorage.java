package net.hobbnetwork.storage;

import net.hobbnetwork.managers.HookManager;
import net.hobbnetwork.utils.LogUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * A class for handling YML configuration files
 */
public class YMLStorage extends Storage {
  private final HookManager hookManager;
  private FileConfiguration config;
  private File configFile;
  private String configName;


  /**
   * This constructor creates a new YMLStorage object
   * Note that this method uses the hooked plugin instance as Data Folder
   * @param hookManager The HookManager object
   * @param configName The name of the configuration file
   * @param path The optional path to the configuration file
   */
  public YMLStorage(HookManager hookManager, @NotNull String configName, @Nullable String... path) {
    this.hookManager = hookManager;
    init(configName, path);
  }


  /**
   * This method creates a new configuration file named configName.yml
   * Note that this method uses the hooked plugin instance as Data Folder
   * @param configName The name of the configuration file
   * @param path The optional path to the configuration file
   * @return The FileConfiguration object
   */
  @Override
  public CompletableFuture<Boolean> init(@NotNull String configName, String... path) {
    String paths = path.length > 0 && path[0] != null ? path[0] : "yml-storage";
    try {
      this.configName = configName;
      if(!hookManager.isHooked()) return CompletableFuture.completedFuture(false);
      File configPath = new File(hookManager.getPlugin().getDataFolder(), paths);
      if(!configPath.exists()) {
        boolean suc = configPath.mkdirs();
        if(!suc) throw new IOException("[YMLStorage] Failed to create directory "+configPath);
      }
      this.configFile = new File(configPath, configName);
      if(!configFile.exists()) {
        boolean suc = configFile.getParentFile().mkdirs();
        if(suc) hookManager.getPlugin().saveResource(configName, false);
      }
      this.config = new YamlConfiguration();
      hookManager.log(LogUtil.LogLevel.DEBUG, "[YMLStorage] Initializing " + config + " with "+configFile);
      try {
        config.load(configFile);
        hookManager.log(Level.FINEST, "[YMLStorage] "+configName + ".yml loaded successfully");
        return CompletableFuture.completedFuture(true);
      } catch(IOException | InvalidConfigurationException e) {
        hookManager.log(Level.SEVERE, "[YMLStorage] "+hookManager.getPlugin().getDataFolder()+"\\"+paths+"\\"+configName + ".yml failed to load!", e);
        return CompletableFuture.completedFuture(false);
      }
    } catch(Exception e) {
      hookManager.log(Level.SEVERE, "[YMLStorage] Failed to initialize " + configName + ".yml", e.getStackTrace(), hookManager.getPlugin(), hookManager.getPlugin().getDataFolder(), paths);
      return CompletableFuture.completedFuture(false);
    }
  }
  /**
   * This method sets the value of a key and saves the configuration file using {@link #save()}
   * @param tkv The key-value pair to set the value of
   * @param value The value to set
   * @return True if the value was set, false otherwise
   */
  @Override
  public CompletableFuture<Boolean> setValue(@NotNull TypedKeyValue<?> tkv, @Nullable Object value) {
    this.config.set(tkv.getKey(), value);
    return CompletableFuture.completedFuture(save());
  }
  /**
   * This method gets the value of a key
   * @param tkv The key-value pair to get the value of
   * @return The value of the key or null if it does not exist
   */
  @Override
  public CompletableFuture<Object> getValue(@NotNull TypedKeyValue<?> tkv) {
    return CompletableFuture.completedFuture(tkv.getType().cast(this.config.get(tkv.getKey())));
  }
  /**
   * This method removes the value of a key and saves the configuration file using {@link #save()}
   * @param tkv The key-value pair to remove the value of
   * @return True if the value was removed, false otherwise
   */
  @Override
  public CompletableFuture<Boolean> removeValue(@NotNull TypedKeyValue<?> tkv) {
    this.config.set(tkv.getKey(), null);
    return CompletableFuture.completedFuture(save());
  }
  /**
   * This method closes the configuration file by saving it
   */
  @Override
  public void close() {
    save();
  }


  /**
   * This method saves the configuration file
   * @return Whether the configuration file was saved successfully
   */
  public boolean save() {
    try {
      config.save(configFile);
      return true;
    } catch(IOException e) {
      hookManager.log(Level.SEVERE, "[YMLStorage] Failed to save " + configName + ".yml", e);
      return false;
    }
  }
  /**
   * This method gets a {@link ConfigurationSection} or creates a new one if it doesn't exist
   * @param section The name of the section
   * @return The ConfigurationSection object
   */
  @NotNull public ConfigurationSection getSection(@NotNull String section) {
    ConfigurationSection result = config.getConfigurationSection(section) == null
      ? config.createSection(section)
      : config.getConfigurationSection(section);
    save();
    return result;
  }
  /**
   * This method reloads the {@link FileConfiguration} object.
   * Useful for when the file is changed externally
   */
  public void reload() {
    this.config = YamlConfiguration.loadConfiguration(configFile);
  }


  // STATIC UTILITY METHODS:


  /**
   * This method creates a new YMLStorage object with the given configuration file name
   * @param hookManager The HookManager object
   * @param configName The name of the configuration file
   * @param path The optional path to the configuration file
   * @return The YMLStorage object
   */
  @NotNull public static YMLStorage createConfig(HookManager hookManager, @NotNull String configName, String... path) {
    return new YMLStorage(hookManager, configName, path);
  }

}
