package net.hobbnetwork.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class SerializationUtil {
  /**
   * This method is used to get a location from a string
   * The string should be in the format "Location{world=WorldName, x=X, y=Y, z=Z, yaw=Yaw, pitch=Pitch}"
   * or "Location{world=WorldName, x=X, y=Y, z=Z}"
   * Internally, this method uses {@link Bukkit#getWorld(String)} to get the world and the other values are parsed as doubles
   * @param s The string to parse
   * @return The location or null if the string is not a location
   */
  @Nullable
  static public Location getLocation(@Nullable String s) {
    if(s == null || !s.startsWith("Location{")) return null;
    String[] fields = s.substring(9, s.length()-1).split(",");
    if(fields.length == 4) return new Location(Bukkit.getWorld(fields[0].substring(fields[0].lastIndexOf("=")+1, fields[0].length()-1)), Double.parseDouble(fields[1].substring(fields[1].lastIndexOf("=")+1)), Double.parseDouble(fields[2].substring(fields[2].lastIndexOf("=")+1)), Double.parseDouble(fields[3].substring(fields[3].lastIndexOf("=")+1)));
    if(fields.length == 6) return new Location(Bukkit.getWorld(fields[0].substring(fields[0].lastIndexOf("=")+1, fields[0].length()-1)), Double.parseDouble(fields[1].substring(fields[1].lastIndexOf("=")+1)), Double.parseDouble(fields[2].substring(fields[2].lastIndexOf("=")+1)), Double.parseDouble(fields[3].substring(fields[3].lastIndexOf("=")+1)), Float.parseFloat(fields[4].substring(fields[4].lastIndexOf("=")+1)), Float.parseFloat(fields[5].substring(fields[5].lastIndexOf("=")+1)));
    return null;
  }
  /**
   * This method is used to get a list of locations from a string
   * The string should be in the format "[Location1, Location2, Location3, ...]"
   * Internally, this method uses {@link SerializationUtil#getLocation(String)} to parse the locations
   * @param s The string to parse
   * @return The list of locations or null if the string is not a list of locations
   */
  @Nullable
  static public ArrayList<Location> getLocationList(@Nullable String s) {
    if(s == null || !s.startsWith("[")) return null;
    String[] items = s.substring(1, s.length()-1).split(", ");
    return Arrays.stream(items).map(SerializationUtil::getLocation).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
  }
  /**
   * This method is used to get a list of UUIDs from a string
   * The string should be in the format "[UUID1, UUID2, UUID3, ...]"
   * Internally, this method uses {@link UUID#fromString(String)} to parse the UUIDs
   * @param s The string to parse
   * @return The list of UUIDs or null if the string is not a list of UUIDs
   */
  @Nullable
  static public ArrayList<UUID> getUUIDList(@Nullable String s) {
    if(s == null || !s.startsWith("[")) return null;
    String[] items = s.substring(1, s.length()-1).split(", ");
    return Arrays.stream(items)
      .map(id -> {
        try {
          return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
          return null;
        }
      })
      .filter(Objects::nonNull)
      .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
  }
}
