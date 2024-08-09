package net.hobbnetwork.storage;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * There are quite some Bukkit classes that are not serializable, so we need to create our own serializers for them.
 * This class contains the serializers for the following Bukkit classes:
 * <br>- {@link Location}
 * <br>- {@link World}
 * <br>- {@link Player}
 * <br>- {@link ItemMeta}
 * <br>- {@link ItemStack}
 */
public class BukkitSerializer {
  /**
   * Checks if the object is a string and if it is, it checks if it is a serialized Bukkit object.
   * If it is, it deserializes it and returns the deserialized object.
   * @param o The object to check
   * @param c The classes to check for
   * @return The deserialized object if it was serialized, otherwise the original object
   */
  public static Object get(Object o, Class<?>... c) {
    if(!(o instanceof String s)) return o;
    if(s.startsWith("Location")) return getLocation(s);
    if(s.startsWith("World")) return getWorld(s);
    if(s.startsWith("Player")) return getPlayer(s);
    if(s.startsWith("ItemMeta")) return getItemMeta(s);
    if(s.startsWith("ItemStack")) return getItemStack(s);
    return o;
  }
  /**
   * Checks if the object is a Bukkit object and if it is, it serializes it.
   * @param o The object to check
   * @return The serialized object if it was a Bukkit object, otherwise the original object
   */
  public static Object set(Object o) {
    if(o instanceof Location) return setLocation((Location) o);
    if(o instanceof World) return setWorld((World) o);
    if(o instanceof Player) return setPlayer((Player) o);
    if(o instanceof ItemMeta) return setItemMeta((ItemMeta) o);
    if(o instanceof ItemStack) return setItemStack((ItemStack) o);
    return o;
  }

  private static Location getLocation(String l) {
    String[] parts = l.replace("Location{", "").replace("}", "").split(",");
    return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
  }
  private static String setLocation(Location l) {
    return "Location{" + l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch() + "}";
  }
  private static String setWorld(World world) {
    return world.getName();
  }
  private static World getWorld(String world) {
    return Bukkit.getWorld(world);
  }
  private static String setPlayer(Player player) {
    return player.getUniqueId().toString();
  }
  private static Player getPlayer(String uuid) {
    return Bukkit.getPlayer(UUID.fromString(uuid));
  }
  private static String setItemMeta(ItemMeta meta) {
    return new Gson().toJson(meta);
  }
  private static ItemMeta getItemMeta(String meta) {
    return new Gson().fromJson(meta, ItemMeta.class);
  }
  private static String setItemStack(ItemStack stack) {
    return new Gson().toJson(stack);
  }
  private static ItemStack getItemStack(String stack) {
    return new Gson().fromJson(stack, ItemStack.class);
  }
  private static String setList(List<?> list) {
    return "List{"+list.stream().map(BukkitSerializer::set).collect(ArrayList::new, List::add, List::addAll)+"}";
  }
  private static List<?> getList(String list) {
    if(!list.startsWith("List{") || !list.endsWith("}")) return new ArrayList<>();
    return Arrays.stream(list.substring(5, list.length() - 1).split(", ")).map(BukkitSerializer::get).collect(ArrayList::new, List::add, List::addAll);
  }

}
