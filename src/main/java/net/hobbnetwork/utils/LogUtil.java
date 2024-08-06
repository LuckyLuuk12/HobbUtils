package net.hobbnetwork.utils;

import lombok.Getter;
import net.hobbnetwork.managers.HookManager;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
  private final HookManager hookManager;
  public LogUtil(HookManager hookManager) {
    this.hookManager = hookManager;
    setupLogger();
  }
  /**
   * This method sets up the logger for the plugin
   */
  private void setupLogger() {
    Logger logger = hookManager.isHooked() ? hookManager.getPlugin().getLogger() : Logger.getGlobal();
    // Remove existing handlers to avoid duplicate logging
    for(var handler : logger.getHandlers()) {
      logger.removeHandler(handler);
    }
    logger.setUseParentHandlers(false);
    ConsoleHandler handler = new ConsoleHandler();
    handler.setLevel(Level.ALL);
    handler.setFormatter(new ColorFormatter());
    logger.addHandler(handler);
    logger.setLevel(Level.ALL);
  }
  /**
   * This method logs a message to the console using the plugin's logger if it is hooked
   *
   * @param lvl     The level of the message
   * @param message The message to log
   * @see LogLevel
   */
  public void log(Level lvl, Object... message) {
    Logger logger = hookManager.getPlugin().getLogger();
    StringBuilder msg = new StringBuilder();
    for(Object e : message) {
      msg.append(e.toString()).append("\n");
    }
    logger.log(lvl, msg.toString());
  }

  /**
   * This class is used to define custom log levels with colors
   * <ul>
   *   <li>{@link LogLevel#BEST}   - The best log level</li>
   *   <li>{@link LogLevel#BETTER} - A better log level</li>
   *   <li>{@link LogLevel#GOOD}   - A good log level</li>
   *   <li>{@link LogLevel#DEBUG}  - A debug log level</li>
   *   <li>{@link LogLevel#TEST}   - A test log level</li>
   *   <li>{@link LogLevel#TEXT}   - A text log level</li>
   *   <li>{@link LogLevel#WARN}   - A warning log level</li>
   *   <li>{@link LogLevel#ERROR}  - An error log level</li>
   *   <li>{@link LogLevel#CRASH}  - A crash log level</li>
   * </ul>
   */
  @Getter
  public static class LogLevel extends Level {
    public static final LogLevel BEST = new LogLevel("BEST", 300, "045300");
    public static final LogLevel BETTER = new LogLevel("BETTER", 400, "2ea025");
    public static final LogLevel GOOD = new LogLevel("GOOD", 500, "bcff9e");
    public static final LogLevel DEBUG = new LogLevel("DEBUG", 600, "ce70ff");
    public static final LogLevel TEST = new LogLevel("TEST", 700, "ff00ff");
    public static final LogLevel TEXT = new LogLevel("TEXT", 800, "FBFBFB");
    public static final LogLevel WARN = new LogLevel("WARN", 900, "eeef7a");
    public static final LogLevel ERROR = new LogLevel("ERROR", 1000, "ff8994");
    public static final LogLevel CRASH = new LogLevel("CRASH", 1100, "7d1c25");

    private final String color;

    protected LogLevel(String name, int value, String color) {
      super(name, value);
      this.color = color;
    }

    /**
     * This method translates a {@link Level} to a {@link LogLevel}. If the level is an instance of {@link LogLevel}, it will return itself.
     * @param lvl The level to translate
     * @return Itself if the level is a {@link LogLevel}, otherwise a new {@link LogLevel} based on the {@link Level}
     */
    public static LogLevel translate(Level lvl) {
      if(lvl instanceof LogLevel l) return l;
      else if(lvl.equals(Level.ALL)) return CRASH;
      else if(lvl.equals(Level.SEVERE)) return ERROR;
      else if(lvl.equals(Level.WARNING)) return WARN;
      else if(lvl.equals(Level.INFO)) return TEXT;
      else if(lvl.equals(Level.CONFIG)) return TEST;
      else if(lvl.equals(Level.FINE)) return GOOD;
      else if(lvl.equals(Level.FINER)) return BETTER;
      else if(lvl.equals(Level.FINEST)) return BEST;
      else return TEXT;
    }

  }

  private class ColorFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
      String color = (LogLevel.translate(record.getLevel())).getColor();
      String pluginName = hookManager.getPlugin().getName();
      String pluginColor = String.format("\u001B[38;2;%s;%s;%sm",
        Integer.valueOf(hookManager.getPrefixHex().substring(0, 2), 16),
        Integer.valueOf(hookManager.getPrefixHex().substring(2, 4), 16),
        Integer.valueOf(hookManager.getPrefixHex().substring(4, 6), 16)
      );
      String resetColor = "\u001B[0m";
      String[] lines = formatMessage(record).split("\n");
      StringBuilder formattedMessage = new StringBuilder();
      for (String line : lines) {
        formattedMessage.append(String.format("%s[%s]%s \u001B[38;2;%s;%s;%sm%s%s\n",
          pluginColor, pluginName, resetColor,
          Integer.valueOf(color.substring(0, 2), 16),
          Integer.valueOf(color.substring(2, 4), 16),
          Integer.valueOf(color.substring(4, 6), 16),
          line, resetColor));
      }
      return formattedMessage.toString();
    }
  }

}
