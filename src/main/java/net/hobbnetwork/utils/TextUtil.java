package net.hobbnetwork.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class for handling text and messages
 */
public class TextUtil {
  /**
   * Parses a Minecraft string into a Component
   * Basically just a wrapper for {@link LegacyComponentSerializer#legacyAmpersand()}
   * @param s String to parse
   * @return Component from the string or an empty Component if the string is null
   */
  static public Component parseMcString(@Nullable String s) {
    return s == null ? Component.empty() : LegacyComponentSerializer.legacyAmpersand().deserialize(s);
  }
  /**
   * Replaces %player% with the player's name
   * Replaces %player% with "null" if the player is null
   * @param s String to replace in
   * @param p Player to get name from
   * @return String with %player% replaced with player's name
   */
  static public String replacePlayerPlaceholder(@NotNull String s, @Nullable Player p) {
    return s.replaceAll("%player%", p == null ? "null" : p.getName());
  }


  /**
   * Sends a message to a player, uses {@link #parseMcString(String)} to parse the message
   * Does nothing if the player is null
   * @param p Player to send message to
   * @param msg Message to send
   */
  static public void sendPlayer(@Nullable Player p, @Nullable String msg) {
    if(p != null) p.sendMessage(parseMcString(msg));
  }
  /**
   * Sends a message to a player, uses {@link #parseMcString(String)} to parse the message after using
   * Does nothing if the player is null
   * {@link #getTextOf(Component)} to get the string from the Component
   * @param p Player to send message to
   * @param msg Message to send
   */
  static public void sendPlayer(@Nullable Player p, @Nullable Component msg) {
    if(p != null) p.sendMessage(parseMcString(getTextOf(msg)));
  }
  /**
   * Sends a message to a player's action bar, uses {@link #parseMcString(String)} to parse the message
   * Does nothing if the player is null
   * @param p Player to send message to
   * @param msg Message to send
   */
  static public void sendPlayerActionbar(@Nullable Player p, @Nullable String msg) {
    if(p != null) p.sendActionBar(parseMcString(msg));
  }
  /**
   * Sends a message to a player's action bar, uses {@link #parseMcString(String)} to parse the message after using
   * Does nothing if the player is null
   * {@link #getTextOf(Component)} to get the string from the Component
   * @param p Player to send message to
   * @param msg Message to send
   */
  static public void sendPlayerActionbar(@Nullable Player p, @Nullable Component msg) {
    if(p != null) p.sendActionBar(parseMcString(getTextOf(msg)));
  }
  /**
   * Sends a title to a player, uses {@link #parseMcString(String)} to parse the title and subtitle
   * Does nothing if the player is null
   * @param p Player to send title to
   * @param title Title to send
   * @param subtitle Subtitle to send
   */
  static public void sendPlayerTitle(Player p, String title, Duration fadeIn, Duration stay, Duration fadeOut, String... subtitle) {
    if(p == null || title == null || fadeIn == null || stay == null || fadeOut == null) return;
    String sTitle = (subtitle.length >= 1) ? subtitle[0] : "";
    p.showTitle(Title.title(parseMcString(title), parseMcString(sTitle), Title.Times.times(fadeIn, stay, fadeOut)));
  }


  /**
   * Converts a Component to a string
   * Basically just a wrapper for {@link PlainTextComponentSerializer#plainText()}
   * @param c Component to convert
   * @return String from the Component or an empty string if the Component is null
   */
  static public String getTextOf(@Nullable Component c) {
    return c == null ? "" : PlainTextComponentSerializer.plainText().serialize(c);
  }
  /**
   * Capitalizes the first letter of each word in a string
   * @param s String to capitalize
   * @return String with first letter of each word capitalized, or null if the string is null
   */
  @Nullable static public String getCapitalizedString(@Nullable String s) {
    if(s == null) return null;
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
   * @return String formatted based on show booleans in this order: world, x, y, z or null if the location is null
   */
  @Nullable static public String getPrettyLoc(@Nullable Location loc, boolean... show) {
    if(loc == null) return null;
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
  static public String getPrettyIntSeconds(int totalSeconds) {
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
  @Nullable static public String getItemName(@Nullable ItemStack item) {
    if(item == null) return null;
    return (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
      ? getTextOf(item.getItemMeta().displayName())
      : getCapitalizedString(item.getType().name().toLowerCase());
  }
  /**
   * Gets a colored {@link TextComponent} from a hex color code and a message
   * @param hex The hex color code
   * @param msg The message to color
   * @return Colored message or an empty Component if either the hex or the message is null
   */
  static public TextComponent getColoredString(@Nullable String hex, @Nullable String msg) {
    if(hex == null || msg == null) return Component.empty();
    return Component.empty().color(TextColor.fromHexString(hex)).append(Component.text().content(msg));
  }
  /**
   * Adds a string to a {@link TextComponent}
   * @param tc The TextComponent to add the string to
   * @param msg The string to add
   * @return The TextComponent with the string appended or the TextComponent if either the TextComponent or the string is null
   */
  @Nullable static public TextComponent addString(@Nullable TextComponent tc, @Nullable String msg) {
    if(tc == null || msg == null) return tc;
    return tc.append(Component.text().content(msg));
  }
  /**
   * Takes strings and splits them by ", " and adds them to a list
   * Example: getCommaStringList("a, b", "c, d") returns ["a", "b", "c", "d"]
   * @param strings Strings to split and combine
   * @return List of strings
   */
  static public List<String> getCommaStringList(@Nullable String... strings) {
    List<String> res = new ArrayList<>();
    if(strings == null) return res;
    for(String string : strings) {
      Collections.addAll(res, string.split(", "));
    }
    return res;
  }
  /**
   * Gets the direction a player is facing as a {@link TextComponent} with all 16 directions
   * @param p Player to get direction of
   * @return {@link TextComponent} with the direction the player is facing or an empty Component if the player is null
   */
  static public TextComponent getColoredPlayerDirection(@Nullable Player p) {
    if(p == null) return Component.empty();
    TextComponent dir;
    float y = p.getLocation().getYaw();
    if( y < 0 ){y += 360;}
    y %= 360;
    int i = (int)((y+8) / 22.5);
    if(i == 0)      {dir = getColoredString("#6ebe44","South");}              // zuid
    else if(i ==  1){dir = getColoredString("#6fc178","South-Southwest");}    // zuid zuidwest
    else if(i ==  2){dir = getColoredString("#270898","Southwest");}          // zuidwest
    else if(i ==  3){dir = getColoredString("#53c4c8","West-Southwest");}     // west zuidwest
    else if(i ==  4){dir = getColoredString("#044651","West");}               // west
    else if(i ==  5){dir = getColoredString("#283087","West-Northwest");}     // west noordwest
    else if(i ==  6){dir = getColoredString("#684b9e","Northwest");}          // noordwest
    else if(i ==  7){dir = getColoredString("#803092","North-Northwest");}    // noord noordwest
    else if(i ==  8){dir = getColoredString("#92241d","North");}              // noord
    else if(i ==  9){dir = getColoredString("#e74124","North-Northeast");}    // noord noordoost
    else if(i == 10){dir = getColoredString("#db5c26","Northeast");}          // noordoost
    else if(i == 11){dir = getColoredString("#b46627","East-Northeast");}     // oost noordoost
    else if(i == 12){dir = getColoredString("#cc952a","East");}               // oost
    else if(i == 13){dir = getColoredString("#deb625","East-Southeast");}     // oost zuidoost
    else if(i == 14){dir = getColoredString("#868433","Southeast");}          // zuidoost
    else if(i == 15){dir = getColoredString("#919e39","South-Southeast");}    // zuid zuidoost*/
    else            {dir = getColoredString("#6ebe44","South");}              // zuid
    return dir;
  }
  /**
   * Gets the direction a player is facing as a {@link TextComponent}, limited to the cardinal directions
   * @param p Player to get direction of
   * @return {@link TextComponent} with the direction the player is facing or an empty Component if the player is null
   */
  static public TextComponent getLimitedColoredPlayerDirection(@Nullable Player p) {
    if(p == null) return Component.empty();
    TextComponent dir;
    float y = p.getLocation().getYaw();
    if( y < 0 ){y += 360;}
    y %= 360;
    int i = (int)((y+8) / 45);
    if(i == 0)     {dir = getColoredString("#6ebe44","South");}              // zuid
    else if(i == 1){dir = getColoredString("#270898","Southwest");}          // zuidwest
    else if(i == 2){dir = getColoredString("#044651","West");}               // west
    else if(i == 3){dir = getColoredString("#684b9e","Northwest");}          // noordwest
    else if(i == 4){dir = getColoredString("#92241d","North");}              // noord
    else if(i == 5){dir = getColoredString("#db5c26","Northeast");}          // noordoost
    else if(i == 6){dir = getColoredString("#cc952a","East");}               // oost
    else if(i == 7){dir = getColoredString("#868433","Southeast");}          // zuidoost
    else           {dir = getColoredString("#6ebe44","South");}              // zuid
    return dir;
  }
  /**
   * Gets the time of day as a {@link TextComponent}
   * @param p Player to get time of
   * @return {@link TextComponent} with the time of day or an empty Component if the player is null formated as "HH:MM"
   */
  static public TextComponent getTime(Player p) {
    double tick = (double) p.getWorld().getTime();
    int hours = (int) ((tick + 6000) / 1000) % 24;
    int minutes = (int) ((tick % 1000) * 0.06);
    String part1 = (hours < 10) ? "0" : "";
    String part2 = (minutes < 10) ? ":0" : ":";
    p.getWorld().isDayTime();
    return getColoredString(p.getWorld().isDayTime() ? "#f2f251" : "#4a4a4a", part1 + hours + part2 + minutes);
  }
  /**
   * Gets the biome a player is in as a {@link TextComponent}
   * @param biomeName Name of the biome
   * @return {@link TextComponent} with the biome name or an empty Component if the biome name is null
   */
  static public TextComponent getColorizedBiome(@Nullable String biomeName) {
    if(biomeName == null) return Component.empty();
    TextComponent result;
    String name = biomeName.replace('_', ' ');
    // END BIOMES:
    result = switch(biomeName) {
      // End biomes
      case "END_BARRENS" -> getColoredString("#5d4a4a", name);
      case "END_HIGHLANDS" -> getColoredString("#755e5e", name);
      case "END_MIDLANDS" -> getColoredString("#736262", name);
      case "SMALL_END_ISLANDS" -> getColoredString("#937878", name);
      case "THE_END" -> getColoredString("#636363", name);
      case "THE_VOID" -> getColoredString("#000000", name);
      // Nether biomes
      case "BASALT_DELTAS" -> getColoredString("#2e3836", name);
      case "CRIMSON_FOREST" -> getColoredString("#7c2929", name);
      case "NETHER_WASTES" -> getColoredString("#6e6e6e", name);
      case "SOUL_SAND_VALLEY" -> getColoredString("#8c7d8c", name);
      case "WARPED_FOREST" -> getColoredString("#44407e", name);
      // Overworld biomes
      case "BADLANDS" -> getColoredString("#d94515", name);
      case "BADLANDS_PLATEAU" -> getColoredString("#b09765", name);
      case "BAMBOO_JUNGLE" -> getColoredString("#537b09", name);
      case "BAMBOO_JUNGLE_HILLS" -> getColoredString("#2e4209", name);
      case "BEACH" -> getColoredString("#f2e7c4", name);
      case "BIRCH_FOREST" -> getColoredString("#4e8234", name);
      case "BIRCH_FOREST_HILLS" -> getColoredString("#3b6e2e", name);
      case "COLD_OCEAN" -> getColoredString("#44427e", name);
      case "DARK_FOREST" -> getColoredString("#005b00", name);
      case "DEEP_COLD_OCEAN" -> getColoredString("#243b55", name);
      case "DEEP_FROZEN_OCEAN" -> getColoredString("#39ace7", name);
      case "DEEP_LUKEWARM_OCEAN" -> getColoredString("#45adf2", name);
      case "DEEP_OCEAN" -> getColoredString("#00008b", name);
      case "DESERT" -> getColoredString("#fa9418", name);
      case "DESERT_HILLS" -> getColoredString("#c2b280", name);
      case "DESERT_LAKES" -> getColoredString("#e0cda7", name);
      case "FOREST" -> getColoredString("#056621", name);
      case "FLOWER_FOREST" -> getColoredString("#2d8e49", name);
      case "FROZEN_RIVER" -> getColoredString("#a4ebf3", name);
      case "FROZEN_OCEAN" -> getColoredString("#909cea", name);
      case "GIANT_SPRUCE_TAIGA" -> getColoredString("#598556", name);
      case "GIANT_SPRUCE_TAIGA_HILLS" -> getColoredString("#478446", name);
      case "GIANT_TREE_TAIGA" -> getColoredString("#6b8e23", name);
      case "GIANT_TREE_TAIGA_HILLS" -> getColoredString("#556b2f", name);
      case "GRAVELLY_MOUNTAINS" -> getColoredString("#787878", name);
      case "ICE_SPIKES" -> getColoredString("#afffff", name);
      case "JUNGLE" -> getColoredString("#007d21", name); // Diep groen
      case "JUNGLE_EDGE" -> getColoredString("#88a825", name); // Licht groen
      case "JUNGLE_HILLS" -> getColoredString("#558527", name); // Donkerder groen
      case "LUKEWARM_OCEAN" -> getColoredString("#00d8ff", name); // Helder blauw
      case "MOUNTAINS" -> getColoredString("#7f7676", name); // Donker grijs
      case "MUSHROOM_FIELDS" -> getColoredString("#ff00ff", name); // Fel roze
      case "MUSHROOM_FIELD_SHORE" -> getColoredString("#a000ff", name); // Paars
      case "OCEAN" -> getColoredString("#0000cd", name); // Middelblauw
      case "PLAINS" -> getColoredString("#f7e9a3", name); // Lichtgeel
      case "RIVER" -> getColoredString("#0000ff", name); // Blauw
      case "SAVANNA" -> getColoredString("#bdb25f", name); // Geelbruin
      case "SAVANNA_PLATEAU" -> getColoredString("#a79d64", name); // Licht geelbruin
      case "SHATTERED_SAVANNA" -> getColoredString("#e5da87", name); // Fel geelbruin
      case "SHATTERED_SAVANNA_PLATEAU" -> getColoredString("#c9c374", name); // Dof geelbruin
      case "SNOWY_BEACH" -> getColoredString("#fffafa", name); // Sneeuwwit
      case "SNOWY_MOUNTAINS" -> getColoredString("#f0f8ff", name); // Zeer licht blauw
      case "SNOWY_TAIGA" -> getColoredString("#bac4b3", name); // Lichtgrijs groen
      case "SNOWY_TAIGA_HILLS" -> getColoredString("#9db1a0", name); // Middengrijs groen
      case "SNOWY_TAIGA_MOUNTAINS" -> getColoredString("#8ca58f", name); // Donkergrijs groen
      case "SNOWY_TUNDRA" -> getColoredString("#fffafa", name); // Sneeuwwit
      case "STONE_SHORE" -> getColoredString("#a2a2a2", name); // Steengrijs
      case "SUNFLOWER_PLAINS" -> getColoredString("#ffff00", name); // Zonnebloemgeel
      case "SWAMP" -> getColoredString("#697d70", name); // Modderig groen
      case "SWAMP_HILLS" -> getColoredString("#537b52", name); // Donker modderig groen
      case "TAIGA" -> getColoredString("#516c51", name); // Donkergroen
      case "TAIGA_HILLS" -> getColoredString("#4b6154", name); // Zeer donkergroen
      case "TAIGA_MOUNTAINS" -> getColoredString("#3b4f41", name); // Bijna zwartgroen
      case "TALL_BIRCH_FOREST" -> getColoredString("#5d732f", name); // Lichter groen
      case "TALL_BIRCH_HILLS" -> getColoredString("#4c6226", name); // Donkerder lichtgroen
      case "WARM_OCEAN" -> getColoredString("#ff8c00", name); // Warm oranje
      default -> getColoredString("#ffffff", name);
    };
    return result.decorate(TextDecoration.BOLD);
  }
}
