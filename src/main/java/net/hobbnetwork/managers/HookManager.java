package net.hobbnetwork.managers;

import lombok.Getter;
import net.hobbnetwork.listeners.Safeguards;
import net.hobbnetwork.utils.LogUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@Getter
public class HookManager {
  private final boolean hooked;
  private final JavaPlugin plugin;
  private final boolean debug;
  private final String prefixHex;
  /**
   * Use this method to hook a plugin into the Hobb Utils
   * This class will also initiate the {@link Safeguards} class
   * @param plugin The plugin to hook
   * @param options The options to use for the hook <br>
   *                index 0: Whether to enable debug mode
   *                index 1: The prefix color in hex, default is #00fdff
   */
  public HookManager(JavaPlugin plugin, String... options) {
    this.plugin = plugin;
    this.hooked = true;
    this.debug = options.length > 0 && options[0].equals("true");
    this.prefixHex = options.length > 1 && options[1].length() == 6 ? options[1] : "#00fdff";
    new Safeguards(this.plugin);
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
  public void log(LogUtil.LogLevel lvl, Object... message) {
    LogUtil logUtil = new LogUtil(this);
    logUtil.log(lvl, message);
  }

}