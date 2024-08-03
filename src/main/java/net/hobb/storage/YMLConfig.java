package net.hobb.storage;

import net.hobb.HobbUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * A class for handling YML configuration files
 */
public class YMLConfig {
  /**
   * This method creates a new configuration file named configName.yml
   * Note that this method uses the hooked plugin instance as Data Folder
   * @param configName The name of the configuration file
   * @param path The optional path to the configuration file
   * @return The FileConfiguration object
   */
  @Nullable static public FileConfiguration create(@NotNull String configName, String... path) {
    if(HobbUtils.isNotHooked()) return null;
    File actualPath = path.length > 0 ? new File(HobbUtils.getHookedPlugin().getDataFolder(), path[0]) : HobbUtils.getHookedPlugin().getDataFolder();
    File newConfig = new File(actualPath, configName);
    if(!newConfig.exists()) {
      boolean suc = newConfig.getParentFile().mkdirs();    // Create config folder
      if(suc) HobbUtils.getHookedPlugin().saveResource(configName, false);       // create yml file
    }
    FileConfiguration actualConfig = new YamlConfiguration();
    try {
      HobbUtils.getHookedPlugin().getLogger().log(Level.FINER, configName + " loaded successfully");
      actualConfig.load(newConfig);
    } catch(IOException | InvalidConfigurationException e) {
      HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE, configName + " failed to load!", e);
    }
    return actualConfig;
  }


  /**
   * This method saves the configuration file internally using {@link #saveConfig(Object, String...)}
   * @param configName The name of the configuration file
   * @param path The optional path to the configuration file
   */
  @Nullable static public FileConfiguration save(@NotNull String configName, String... path) {
    return saveConfig(configName, path);
  }
  /**
   * This method saves the configuration file
   * @param config The FileConfiguration object
   * @param path The optional path to the configuration file
   */
  @Nullable static public FileConfiguration save(@NotNull FileConfiguration config, String... path) {
    return saveConfig(config, path);
  }
  /**
   * This method saves the configuration file
   * @param obj The FileConfiguration object or the name of the configuration file
   * @param path The optional path to the configuration file
   * @return The FileConfiguration object or null if the object is not a FileConfiguration or a String
   */
  @Nullable static private FileConfiguration saveConfig(@NotNull Object obj, String... path) {
    if(HobbUtils.isNotHooked()) return null;
    if(!(obj instanceof FileConfiguration) && !(obj instanceof String )) return null;
    String configName = (obj instanceof FileConfiguration config) ? config.getName() : (String) obj;
    // Now that we have the configName, we can get the YML and save it
    File actualPath = path.length > 0 ? new File(HobbUtils.getHookedPlugin().getDataFolder(), path[0]) : HobbUtils.getHookedPlugin().getDataFolder();
    File config = new File(actualPath, configName);
    if(!config.exists()) {
      HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE, "Config file " + configName + " does not exist!");
      return null;
    }
    FileConfiguration actualConfig = new YamlConfiguration();
    try {
      actualConfig.save(config);
      HobbUtils.getHookedPlugin().getLogger().log(Level.FINER, configName + " saved successfully");
    } catch(IOException e) {
      HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE, "Failed to save " + configName, e);
    }
    return actualConfig;
  }


  /**
   * This method reloads the configuration file, internally using {@link #reloadConfig(Object, String...)}
   * @param configName The name of the configuration file
   * @param path The optional path to the configuration file
   * @return The FileConfiguration object
   */
  @Nullable static public FileConfiguration reload(@NotNull String configName, String... path) {
    return reloadConfig(configName, path);
  }
  /**
   * This method reloads the configuration file, internally using {@link #reloadConfig(Object, String...)}
   * @param config The FileConfiguration object
   * @param path The optional path to the configuration file
   */
  @Nullable static public FileConfiguration reload(@NotNull FileConfiguration config, String... path) {
    return reloadConfig(config, path);
  }
  /**
   * This method reloads the configuration file
   * @param obj The FileConfiguration object or the name of the configuration file
   * @param path The optional path to the configuration file
   * @return The FileConfiguration object or null if the object is not a FileConfiguration or a String
   */
  @Nullable static private FileConfiguration reloadConfig(@NotNull Object obj, String... path) {
    if(HobbUtils.isNotHooked()) return null;
    if(!(obj instanceof FileConfiguration) && !(obj instanceof String )) return null;
    String configName = (obj instanceof FileConfiguration config) ? config.getName() : (String) obj;
    // Now that we have the configName, we can get the YML and save it
    File actualPath = path.length > 0 ? new File(HobbUtils.getHookedPlugin().getDataFolder(), path[0]) : HobbUtils.getHookedPlugin().getDataFolder();
    File config = new File(actualPath, configName);
    if(!config.exists()) {
      HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE, "Config file " + configName + " does not exist!");
      return null;
    }
    FileConfiguration actualConfig = new YamlConfiguration();
    try {
      actualConfig.load(config);
      HobbUtils.getHookedPlugin().getLogger().log(Level.FINER, configName + " reloaded successfully");
    } catch(IOException | InvalidConfigurationException e) {
      HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE, "Failed to reload " + configName, e);
    }
    return actualConfig;
  }

  /**
   * This method gets a configuration section or creates a new one if it doesn't exist
   * Internally uses {@link #getConfigSection(Object, String, String...)}
   * @param config The FileConfiguration object
   * @param section The name of the section
   * @param path The optional path to the configuration file
   * @return The ConfigurationSection object
   */
  @Nullable static public ConfigurationSection getSection(@NotNull FileConfiguration config, String section, String... path) {
    return getConfigSection(config, section, path);
  }
  /**
   * This method gets a configuration section or creates a new one if it doesn't exist
   * Internally uses {@link #getConfigSection(Object, String, String...)}
   * @param configName The name of the configuration file
   * @param section The name of the section
   * @param path The optional path to the configuration file
   * @return The ConfigurationSection object
   */
  @Nullable static public ConfigurationSection getSection(@NotNull String configName, String section, String... path) {
    return getConfigSection(configName, section, path);
  }
  /**
   * This method gets a configuration section or creates a new one if it doesn't exist
   * @param obj The FileConfiguration object or the name of the configuration file
   * @param section The name of the section
   * @param path The optional path to the configuration file
   * @return The ConfigurationSection object
   */
  @Nullable static public ConfigurationSection getConfigSection(@NotNull Object obj, String section, String... path) {
    if(HobbUtils.isNotHooked()) return null;
    if(!(obj instanceof FileConfiguration) && !(obj instanceof String )) return null;
    String configName = (obj instanceof FileConfiguration config) ? config.getName() : (String) obj;
    // Now that we have the configName, we can get the YML and get the section
    File actualPath = path.length > 0 ? new File(HobbUtils.getHookedPlugin().getDataFolder(), path[0]) : HobbUtils.getHookedPlugin().getDataFolder();
    File config = new File(actualPath, configName);
    if(isNotExistingConfig(configName, path)) return null;
    FileConfiguration actualConfig = new YamlConfiguration();
    if(actualConfig.getConfigurationSection(section) == null) {
      actualConfig.createSection(section);
      saveConfig(actualConfig, path);
    }
    return actualConfig.getConfigurationSection(section);
  }


  /**
   * This method checks if a configuration file exists
   * It also logs a severe message if the configuration file does not exist
   * @param configName The name of the configuration file
   * @param path The optional path to the configuration file
   * @return Whether the configuration file exists
   */
  static private boolean isNotExistingConfig(@NotNull String configName, String... path) {
    if(HobbUtils.isNotHooked()) return true;
    File actualPath = path.length > 0 ? new File(HobbUtils.getHookedPlugin().getDataFolder(), path[0]) : HobbUtils.getHookedPlugin().getDataFolder();
    File newConfig = new File(actualPath, configName);
    if(!newConfig.exists()) HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE, "Config file " + configName + " does not exist!");
    return !newConfig.exists();
  }
}
