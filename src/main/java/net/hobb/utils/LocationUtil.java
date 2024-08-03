package net.hobb.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.jetbrains.annotations.Nullable;

public class LocationUtil {
  /**
   * Checks if a location is in between two other locations
   * @param l The location to check
   * @param sq1 The first corner of the square
   * @param sq2 The second corner of the square
   * @return True if the location is in between the two other locations, square-wise, including the corners
   *        False if any of the locations are null or if the locations are not in the same world or if the location is not in between the two other locations
   */
  static public boolean isInBetween(@Nullable Location l, @Nullable Location sq1, @Nullable Location sq2) {
    if(sq1 == null || sq2 == null || l == null) return false;
    if(sq1.getWorld().equals(sq2.getWorld())){ // Check for worldName location1, location2
      if(l.getWorld().equals(sq1.getWorld())){ // Check for worldName targetLocation, location1
        if((l.getBlockX() >= sq1.getBlockX() && l.getBlockX() <= sq2.getBlockX()) || (l.getBlockX() <= sq1.getBlockX() && l.getBlockX() >= sq2.getBlockX())){ // Check X value
          if((l.getBlockZ() >= sq1.getBlockZ() && l.getBlockZ() <= sq2.getBlockZ()) || (l.getBlockZ() <= sq1.getBlockZ() && l.getBlockZ() >= sq2.getBlockZ())){ // Check Z value
            return (l.getBlockY() >= sq1.getBlockY() && l.getBlockY() <= sq2.getBlockY()) || (l.getBlockY() <= sq1.getBlockY() && l.getBlockY() >= sq2.getBlockY()); // Check Y value
          }
        }
      }
    }
    return false;
  }
  /**
   * Gets the horizontal distance between two locations, thus ignoring the y value
   * @param loc1 The first location
   * @param loc2 The second location
   * @return The distance between the two locations or 0 if either location is null
   */
  static public double getHorizontalDistance(@Nullable Location loc1, @Nullable Location loc2) {
    if(loc1 == null || loc2 == null) return 0;
    return Math.sqrt(Math.pow(loc1.getX()-loc2.getX(), 2)+Math.pow(loc1.getZ()-loc2.getZ(), 2));
  }
  /**
   * Checks if two locations are nearly equal
   * It checks if the worlds are the same and if the x, y, and z values are the same
   * @param loc1 The first location
   * @param loc2 The second location
   * @return True if the coordinates are the same as integers, the worlds are the same, and the locations are not null
   */
  static public boolean isNearlyEqualTo(@Nullable Location loc1, @Nullable Location loc2) {
    return loc1 != null && loc2 != null &&
      loc1.getWorld().equals(loc2.getWorld()) &&
      (int)loc1.getX() == (int)loc2.getX() &&
      (int)loc1.getY() == (int)loc2.getY() &&
      (int)loc1.getZ() == (int)loc2.getZ();
  }
  /**
   * Converts a location to an integer location by casting the doubles to integers
   * It keeps the yaw and pitch the same
   * @param loc Location to convert
   * @return Integer location
   */
  @Nullable static public Location getIntLocation(@Nullable Location loc) {
    if(loc == null) return null;
    return new Location(loc.getWorld(), (int)loc.getX(), (int)loc.getY(), (int)loc.getZ(), loc.getYaw(), loc.getPitch());
  }
  /**
   * Converts a {@link String} to a {@link Location}
   * This works nicely after converting a location to a string with {@link Location#toString()}
   * @param s String to convert
   * @return Location or null if the string is not a location
   */
  @Nullable static public Location getLocFromString(@Nullable String s) {
    if(s == null || !s.startsWith("Location{")) return null;
    String[] fields = s.substring(9, s.length()-1).split(",");
    if(fields.length == 4) return new Location(Bukkit.getWorld(fields[0].substring(fields[0].lastIndexOf("=")+1, fields[0].length()-1)), Double.parseDouble(fields[1].substring(fields[1].lastIndexOf("=")+1)), Double.parseDouble(fields[2].substring(fields[2].lastIndexOf("=")+1)), Double.parseDouble(fields[3].substring(fields[3].lastIndexOf("=")+1)));
    if(fields.length == 6) return new Location(Bukkit.getWorld(fields[0].substring(fields[0].lastIndexOf("=")+1, fields[0].length()-1)), Double.parseDouble(fields[1].substring(fields[1].lastIndexOf("=")+1)), Double.parseDouble(fields[2].substring(fields[2].lastIndexOf("=")+1)), Double.parseDouble(fields[3].substring(fields[3].lastIndexOf("=")+1)), Float.parseFloat(fields[4].substring(fields[4].lastIndexOf("=")+1)), Float.parseFloat(fields[5].substring(fields[5].lastIndexOf("=")+1)));
    return null;
  }
}
