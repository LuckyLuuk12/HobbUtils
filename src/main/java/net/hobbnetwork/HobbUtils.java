package net.hobbnetwork;

import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class HobbUtils extends JavaPlugin {
  @Getter
  private static HobbUtils thisPlugin;
  @Getter
  private static Server host;
  @Getter
  private static Logger console;
  @Getter
  private static JavaPlugin hookedPlugin;


  @Override
  public void onEnable() {
    thisPlugin = this;
    host = this.getServer();
    console = this.getLogger();

  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  /**
   * This method allows other plugins to hook into HobbUtils
   */
  public static void hook(@NotNull Plugin plugin) {
    hookedPlugin = (JavaPlugin) plugin;
    getConsole().log(Level.INFO, plugin.getName()+" Hooked into HobbUtils!");
  }
  /**
   * This method returns whether a plugin is hooked
   * @return Whether a plugin is hooked
   */
  public static boolean isNotHooked() {
    if(hookedPlugin == null) console.severe("Some plugin probably forgot to hook into HobbUtils!");
    return hookedPlugin == null;
  }
}
