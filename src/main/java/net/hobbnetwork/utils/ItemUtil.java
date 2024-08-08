package net.hobbnetwork.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

import static net.hobbnetwork.utils.TextUtil.parseMcString;

public class ItemUtil {

  /**
   * This method creates an ItemStack with the given Material type and Component data.<br>
   * The first Component in the data array is the display name of the item.<br>
   * The rest of the Components in the data array are the lore of the item.<br>
   * @param type The Material type of the item
   * @param data The data to use for the item
   *             Index 0: The display name of the item
   *             Index 1+: The lore of the item
   * @return The ItemStack created
   */
  static public ItemStack getItemStack(Material type, Component... data) {
    ItemStack item = new ItemStack(type);
    ItemMeta im = item.getItemMeta();
    if(data.length > 0) im.displayName(data[0]);
    if(data.length > 1) im.lore(List.of(data).subList(1, data.length));
    item.setItemMeta(im);
    return item;
  }
  /**
   * This method creates an ItemStack with the given Material type and String data.<br>
   * Internally, this method uses {@link TextUtil#parseMcString(String)} to parse it to Component data.<br>
   * And then calls {@link #getItemStack(Material, Component...)} with the parsed Component data.<br>
   * @param type The Material type of the item
   * @param data The data to use for the item
   *             Index 0: The display name of the item
   *             Index 1+: The lore of the item
   * @return The ItemStack created
   */
  static public ItemStack getItemStack(Material type, String... data) {
    return getItemStack(type, Arrays.stream(data).map(TextUtil::parseMcString).toArray(Component[]::new));
  }
  /**
   * This method creates an ItemStack with the given Material type, amount, and Component data.<br>
   * Internally, this method calls {@link #getItemStack(Material, Component...)} and then sets the amount of the item.<br>
   * @param type The Material type of the item
   * @param amount The amount of the item
   * @param data The data to use for the item
   * @return The ItemStack created
   */
  static public ItemStack getItemStack(Material type, int amount, Component... data) {
    ItemStack result = getItemStack(type, data);
    result.setAmount(amount);
    return result;
  }
  /**
   * This method creates an ItemStack with the given Material type, amount, and String data.<br>
   * Internally, this method calls {@link #getItemStack(Material, String...)} and then sets the amount of the item.<br>
   * @param type The Material type of the item
   * @param amount The amount of the item
   * @param Data The data to use for the item
   * @return The ItemStack created
   */
  static public ItemStack getItemStack(Material type, int amount, String... Data) {
    ItemStack result = getItemStack(type, Data);
    result.setAmount(amount);
    return result;
  }

  /**
   * This method changes the owner of a PLAYER_HEAD (skull) ItemStack to the given player.<br>
   * This will change the texture of the skull to the player's head texture.
   * @param itemStack The ItemStack to change the owner of
   * @param player The player to set as the owner of the skull
   */
  static public void setSkullOwner(ItemStack itemStack, Player player) {
    // Ensure the item is a PLAYER_HEAD (skull)
    if (itemStack.getType() != Material.PLAYER_HEAD) return; // Not a player head, do nothing
    SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
    if (skullMeta == null) return; // Failed to get the skull meta, do nothing
    // Set the player's name as the owner of the skull (changes texture)
    skullMeta.setOwningPlayer(player);
    itemStack.setItemMeta(skullMeta);
  }
}
