package net.hobbnetwork.managers;

import lombok.Getter;
import net.hobbnetwork.utils.LogUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class HookManager {
  private final boolean hooked;
  private final JavaPlugin plugin;
  private final boolean debug;
  /**
   * Use this method to hook a plugin into the Hobb Utils
   * @param plugin The plugin to hook
   * @param options The options to use for the hook <br>
   *                index 0: Whether to enable debug mode
   */
  public HookManager(JavaPlugin plugin, boolean... options) {
    this.plugin = plugin;
    this.hooked = true;
    this.debug = options.length > 0 && options[0];
  }
  /**
   * This method logs a message to the console using the plugin's logger if it is hooked
   * Otherwise, it will use the global logger
   * @param level The level of the message
   * @param message The message to log
   */
  public void log(Level level, Object... message) {
    LogUtil logUtil = new LogUtil(this);
    logUtil.log(level, message);
  }

}