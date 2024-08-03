package net.hobb.utils;

import net.hobb.HobbUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Config {

  private FileConfiguration config;
  private File configFile;
  private final String folderName, fileName;

  public Config(final String folderName, final String fileName) {
    this.folderName = folderName;
    this.fileName = fileName;
  }

  /**
   * This method creates a new file
   * @param message The message to log
   * @param header The header to load the config with
   */
  public void createNewFile(final String message, final String header) {
    this.reload();
    this.save();
    this.load(header);

    if (message != null) {
      HobbUtils.getHookedPlugin().getLogger().info(message);
    }
  }

  /**
   * This method gets the config
   * @return The config
   */
  public FileConfiguration getConfig() {
    if (this.config == null) {
      this.reload();
    }
    return this.config;
  }

  /**
   * This method loads the config with a header
   * @param header The header to load the config with
   */
  public void load(final String header) {
    this.config.options().header(header);
    this.config.options().copyDefaults(true);
    this.save();
  }

  /**
   * This method reloads the config
   */
  public void reload() {
    if (this.configFile == null) {
      this.configFile = new File(HobbUtils.getHookedPlugin().getDataFolder() + this.folderName, this.fileName);
    }
    this.config = YamlConfiguration.loadConfiguration(this.configFile);
  }

  /**
   * This method saves the config
   */
  public void save() {
    if (this.config == null || this.configFile == null) return;
    try {
      this.getConfig().save(this.configFile);
    } catch (final IOException ex) {
      HobbUtils.getHookedPlugin().getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, ex);
    }
  }

  /**
   * This method gets a config section or creates a new one if it doesn't exist
   * @param section The section to get or create
   * @return The section
   */
  public ConfigurationSection getSection(String section) {
    FileConfiguration config = getConfig();
    if(!config.isConfigurationSection(section)) {
      config.createSection(section);
      save();
    }
    return config.getConfigurationSection(section);
  }
}
