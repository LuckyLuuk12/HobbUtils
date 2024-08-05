package net.hobbnetwork;

import lombok.Getter;
import net.hobbnetwork.managers.HookManager;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class HobbUtils extends JavaPlugin {
  @Getter
  private static HobbUtils thisPlugin;
  @Getter
  private static Server host;
  @Getter
  private static Logger console;


  @Override
  public void onEnable() {
    thisPlugin = this;
    host = thisPlugin.getServer();
    console = thisPlugin.getLogger();
    console.log(Level.INFO, "Hobb-Utils has been enabled!");
  }

  @Override
  public void onDisable() {
    if(console != null) console.log(Level.INFO, "Hobb-Utils has been disabled!");
    thisPlugin = null;
    host = null;
    console = null;
  }
}
