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
    for (var handler : logger.getHandlers()) {
      logger.removeHandler(handler);
    }
    ConsoleHandler handler = new ConsoleHandler();
    handler.setFormatter(new ColorFormatter());
    logger.addHandler(handler);
  }
  /**
   * This method logs a message to the console using the plugin's logger if it is hooked
   * @see LogLevel
   * @param lvl The level of the message
   * @param message The message to log
   */
  public void log(LogLevel lvl, Object... message) {
    Logger logger = hookManager.getPlugin().getLogger();
    StringBuilder msg = new StringBuilder();
    for (Object e : message) {
      msg.append(e.toString()).append("\n");
    }
    logger.log(lvl, msg.toString());
  }
  /**
   * This method accepts a {@link Level} and converts it to a {@link LogLevel} and then calls {@link #log(LogLevel, Object...)}
   * @param lvl The level of the message
   * @param message The message to log
   */
  public void log(Level lvl, Object... message) {
    if(lvl.equals(Level.SEVERE)) log(LogLevel.CRASH, message);
    else if(lvl.equals(Level.WARNING)) log(LogLevel.WARN, message);
    else if(lvl.equals(Level.INFO)) log(LogLevel.TEXT, message);
    else if(lvl.equals(Level.CONFIG)) log(LogLevel.TEST, message);
    else if(lvl.equals(Level.FINE)) log(LogLevel.GOOD, message);
    else if(lvl.equals(Level.FINER)) log(LogLevel.BETTER, message);
    else if(lvl.equals(Level.FINEST)) log(LogLevel.BEST, message);
    else log(LogLevel.TEXT, message);
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
    public static final LogLevel BEST = new LogLevel("FINEST", 300, "045300");
    public static final LogLevel BETTER = new LogLevel("FINER", 400, "2ea025");
    public static final LogLevel GOOD = new LogLevel("FINE", 500, "bcff9e");
    public static final LogLevel DEBUG = new LogLevel("DEBUG", 600, "ce70ff");
    public static final LogLevel TEST = new LogLevel("TEST", 700, "ff00ff");
    public static final LogLevel TEXT = new LogLevel("INFO", 800, "FBFBFB");
    public static final LogLevel WARN = new LogLevel("WARNING", 900, "eeef7a");
    public static final LogLevel ERROR = new LogLevel("SEVERE", 1000, "ff8994");
    public static final LogLevel CRASH = new LogLevel("CRASH", 1100, "7d1c25");

    private final String color;

    protected LogLevel(String name, int value, String color) {
      super(name, value);
      this.color = color;
    }

  }

  private static class ColorFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
      String color = ((LogLevel) record.getLevel()).getColor();
      return String.format("\u001B[38;2;%s;%s;%sm%s\u001B[0m",
        Integer.valueOf(color.substring(0, 2), 16),
        Integer.valueOf(color.substring(2, 4), 16),
        Integer.valueOf(color.substring(4, 6), 16),
        formatMessage(record));
    }
  }

}
