package net.hobb.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class for handling text and messages
 */
public class Text {
  /**
   * Parses a Minecraft string into a Component
   * Basically just a wrapper for {@link LegacyComponentSerializer#legacyAmpersand()}
   * @param s String to parse
   * @return Component from the string
   */
  static public Component parseMcString(String s) {
    return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
  }
  /**
   * Sends a message to a player, uses {@link #parseMcString(String)} to parse the message
   * @param p Player to send message to
   * @param msg Message to send
   */
  static public void sendPlayer(Player p, String msg) {
    p.sendMessage(parseMcString(msg));
  }
  /**
   * Sends a message to a player, uses {@link #parseMcString(String)} to parse the message after using
   * {@link #getTextOf(Component)} to get the string from the Component
   * @param p Player to send message to
   * @param msg Message to send
   */
  static public void sendPlayer(Player p, Component msg) {
    p.sendMessage(parseMcString(getTextOf(msg)));
  }
  /**
   * Sends a message to a player's action bar, uses {@link #parseMcString(String)} to parse the message
   * @param p Player to send message to
   * @param msg Message to send
   */
  static public void sendPlayerActionbar(Player p, String msg) {
    p.sendActionBar(parseMcString(msg));
  }
  /**
   * Sends a message to a player's action bar, uses {@link #parseMcString(String)} to parse the message after using
   * {@link #getTextOf(Component)} to get the string from the Component
   * @param p Player to send message to
   * @param msg Message to send
   */
  static public void sendPlayerActionbar(Player p, Component msg) {
    p.sendActionBar(parseMcString(getTextOf(msg)));
  }
  /**
   * Sends a title to a player, uses {@link #parseMcString(String)} to parse the title and subtitle
   * @param p Player to send title to
   * @param title Title to send
   * @param subtitle Subtitle to send
   */
  static public void sendPlayerTitle(Player p, String title, Duration fadeIn, Duration stay, Duration fadeOut, String... subtitle) {
    String sTitle = (subtitle.length >= 1) ? subtitle[0] : "";
    p.showTitle(Title.title(parseMcString(title), parseMcString(sTitle), Title.Times.times(fadeIn, stay, fadeOut)));
  }
  /**
   * Converts a Component to a string
   * Basically just a wrapper for {@link PlainTextComponentSerializer#plainText()}
   * @param c Component to convert
   * @return String from the Component
   */
  static public String getTextOf(Component c) {
    return PlainTextComponentSerializer.plainText().serialize(c);
  }
  /**
   * Takes strings and splits them by ", " and adds them to a list
   * Example: getCommaStringList("a, b", "c, d") returns ["a", "b", "c", "d"]
   * @param strings Strings to split and combine
   * @return List of strings
   */
  static public List<String> getCommaStringList(String... strings) {
    List<String> res = new ArrayList<>();
    for(String string : strings) {
      Collections.addAll(res, string.split(", "));
    }
    return res;
  }
  /**
   * Replaces %player% with the player's name
   * @param s String to replace in
   * @param p Player to get name from
   * @return String with %player% replaced with player's name
   */
  static public String replacePlayerPlaceholder(String s, Player p) {
    return s.replaceAll("%player%", p.getName());
  }
  /**
   * Capitalizes the first letter of each word in a string
   * @param s String to capitalize
   * @return String with first letter of each word capitalized
   */
  static public String capitalize(String s) {
    StringBuilder res = new StringBuilder();
    for(String word : s.split(" ")) {
      res.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
    }
    return res.toString();
  }
  /**
   * Converts a location to a pretty string
   * @param loc Location to print prettier
   * @param show show[0] = print_world_name <br> show[1] = show_x <br> show[2] = show_y <br> show[3] = show_z
   * @return String formatted based on show booleans in this order: world, x, y, z
   */
  static public String prettyLoc(Location loc, boolean... show) {
    if(loc == null) return "NULL";
    String world = (show.length >= 1) ? (show[0]) ? loc.getWorld().getName()   : "" : loc.getWorld().getName();
    String x     = (show.length >= 2) ? (show[1]) ? String.format("%.2f",loc.getX()) : "" : String.format("%.2f",loc.getX());
    String y     = (show.length >= 3) ? (show[2]) ? String.format("%.2f",loc.getY()) : "" : String.format("%.2f",loc.getY());
    String z     = (show.length >= 4) ? (show[3]) ? String.format("%.2f",loc.getZ()) : "" : String.format("%.2f",loc.getZ());
    return world+" "+x+" "+y+" "+z;
  }
  /**
   * Converts seconds to a pretty string
   * @param totalSeconds integer amount of seconds to convert
   * @return String formatted as "X Days Y Hours Z Minutes A Seconds"
   */
  static public String prettyIntSeconds(int totalSeconds) {
    int secondsInMinute = 60;
    int secondsInHour = 60 * secondsInMinute;
    int secondsInDay = 24 * secondsInHour;
    int days = totalSeconds / secondsInDay;
    int remainingSecondsAfterDays = totalSeconds % secondsInDay;
    int hours = remainingSecondsAfterDays / secondsInHour;
    int remainingSecondsAfterHours = remainingSecondsAfterDays % secondsInHour;
    int minutes = remainingSecondsAfterHours / secondsInMinute;
    int remainingSeconds = remainingSecondsAfterHours % secondsInMinute;
    return days + " Days " + hours + " Hours " + minutes + " Minutes " + remainingSeconds + " Seconds";
  }
  /**
   * Gets the name of an {@link ItemStack item}
   * @param item ItemStack to get name of
   * @return Name of the item
   */
  static public String getItemName(ItemStack item) {
    return (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) ? getTextOf(item.getItemMeta().displayName()) : capitalize(item.getType().name().toLowerCase());
  }


}
