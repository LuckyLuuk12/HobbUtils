package net.hobbnetwork.managers;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class HookManager {
  private boolean hooked;
  private JavaPlugin plugin;
  private boolean debug;
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
    Logger logger = hooked ? plugin.getLogger() : Logger.getGlobal();
    StringBuilder msg = new StringBuilder();
    for (Object e : message) {
      if (e instanceof Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        msg.append(debug ? sw : t.getMessage()).append("\n");
      } else {
        msg.append(e.toString()).append("\n");
      }
    }
    // If the Level is FINEST, FINE or INFO, log the message in a custom color
    if (level.equals(Level.FINEST) || level.equals(Level.FINE)) { // FINE = Lime
      logger.log(Level.INFO, "\u001B[3am" + msg + "\u001B[0m");
      return;
    }
    if (level.equals(Level.INFO)) { // INFO = Pink
      logger.log(Level.INFO, "\u001B[3dm" + msg + "\u001B[0m");
      return;
    }
    // Log the normal way
    logger.log(level, msg.toString());
  }

}