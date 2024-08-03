package net.hobb.utils;

import net.hobb.HobbUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a very complicated class that offers methods to handle "mechanics" in the game
 * With "mechanics" we mean the way the game works in movement, physics, etc.
 */
public class MechanicsUtil {
  /**
   * This method hides an entity from all players except the ones specified
   * Note that this method does not implement un-hiding on purpose, please use {@link #showEntityForAll(Entity, Player...)} for that
   * @param toBeHidden The entity to be hidden
   * @param except The players that will still see the entity (optional)
   */
  static public void hideEntityForAll(@Nullable Entity toBeHidden, Player... except) {
    if(HobbUtils.isNotHooked()) return;
    if(toBeHidden == null) return;
    ArrayList<Player> exceptions = new ArrayList<>(List.of(except));
    for(Player p : HobbUtils.getHookedPlugin().getServer().getOnlinePlayers()) {
      if(!exceptions.contains(p) ) p.hideEntity(HobbUtils.getHookedPlugin(), toBeHidden);
    }
  }
  /**
   * This method shows an entity to all players except the ones specified
   * @param toBeUnhidden The entity to be shown
   * @param except The players that will not see the entity (optional)
   */
  static public void showEntityForAll(@Nullable Entity toBeUnhidden, Player... except) {
    if(HobbUtils.isNotHooked()) return;
    if(toBeUnhidden == null) return;
    ArrayList<Player> exceptions = new ArrayList<>(List.of(except));
    for(Player p : HobbUtils.getHookedPlugin().getServer().getOnlinePlayers()) {
      if(!exceptions.contains(p) ) p.showEntity(HobbUtils.getHookedPlugin(), toBeUnhidden);
    }
  }


  /**
   * This method moves an entity forward based on the yaw and pitch angles
   * @param entity The entity to move
   * @param speed The speed to move the entity
   */
  static public void moveEntityForward(@Nullable Entity entity, double speed) {
    if(entity == null) return;
    // Get the entity's location
    Location location = entity.getLocation();

    // Calculate the direction vector from the yaw and pitch angles
    double yawRadians = Math.toRadians(location.getYaw());
    double pitchRadians = Math.toRadians(location.getPitch());
    double x = -Math.sin(yawRadians) * Math.cos(pitchRadians);
    double y = (entity.isOnGround()) ? -Math.sin(pitchRadians) : -1;
    double z = Math.cos(yawRadians) * Math.cos(pitchRadians);

    // Create the movement vector based on the direction and speed
    Vector direction = new Vector(x, y, z).normalize();
    double m = (y > 0) ? 0.5 : 1;
    Vector velocity = direction.multiply(m*speed);

    // Apply the velocity to the entity
    entity.setVelocity(velocity);
  }
  /**
   * This method moves an entity forward based on the yaw and pitch angles, but does not allow the entity to automatically jump
   * @param entity The entity to move
   * @param speed The speed to move the entity
   */
  static public void moveEntityForwardNoJump(@Nullable Entity entity, double speed) {
    if(entity == null) return;
    // Get the entity's location
    Location location = entity.getLocation();

    // Calculate the direction vector from the yaw and pitch angles
    double yawRadians = Math.toRadians(location.getYaw());
    double pitchRadians = Math.toRadians(location.getPitch());
    double x = -Math.sin(yawRadians) * Math.cos(pitchRadians);
    double y = 0;
    double z = Math.cos(yawRadians) * Math.cos(pitchRadians);

    // Create the movement vector based on the direction and speed
    Vector direction = new Vector(x, y, z).normalize();
    Vector velocity = direction.multiply(speed);

    // Apply the velocity to the entity
    entity.setVelocity(velocity);
  }


  @NotNull static public Rotation getNextRotation(@NotNull Rotation r) {
    if(r.equals(Rotation.NONE)) return Rotation.CLOCKWISE_45;
    if(r.equals(Rotation.CLOCKWISE_45)) return Rotation.CLOCKWISE;
    if(r.equals(Rotation.CLOCKWISE)) return Rotation.CLOCKWISE_135;
    if(r.equals(Rotation.CLOCKWISE_135)) return Rotation.FLIPPED;
    if(r.equals(Rotation.FLIPPED)) return Rotation.FLIPPED_45;
    if(r.equals(Rotation.FLIPPED_45)) return Rotation.COUNTER_CLOCKWISE;
    if(r.equals(Rotation.COUNTER_CLOCKWISE)) return Rotation.COUNTER_CLOCKWISE_45;
    return Rotation.NONE;
  }


  /**
   * This method swaps the items in the main and offhand of a player
   * @param p The player to swap the items for
   */
  static public void swapOffMain(@Nullable Player p) {
    if(p == null) return;
    ItemStack i1 = p.getInventory().getItemInMainHand();
    ItemStack i2 = p.getInventory().getItemInOffHand();
    p.getInventory().setItemInMainHand(i2);
    p.getInventory().setItemInOffHand(i1);
  }
  /**
   * This method swaps the items in the helmet and main hand of a player
   * @param p The player to swap the items for
   */
  static public void swapHatMain(@Nullable Player p) {
    if(p == null) return;
    ItemStack i1 = p.getInventory().getItemInMainHand();
    ItemStack i2 = p.getInventory().getItem(EquipmentSlot.HEAD);
    p.getInventory().setItemInMainHand(i2);
    p.getInventory().setItem(EquipmentSlot.HEAD, i1);
  }
  /**
   * This method checks if an inventory contains a certain item stack with a certain amount
   * @param pi The player inventory to check
   * @param is The item stack to check for
   * @param amount The amount to check for
   * @return True if the inventory contains the item stack with the amount
   */
  static public boolean inventoryContainsItemStack(@Nullable PlayerInventory pi, @Nullable  ItemStack is, int amount) {
    if(pi == null || is == null) return false;
    for(ItemStack item : pi) {
      if(item == null) continue;
      if(item.getType().equals(is.getType()) && item.getAmount() >= amount
        || shulkerContainsItemStack(item, is, amount)
        || bundleContainsItemStack(item, is, amount)) return true;
    }
    return false;
  }
  /**
   * This method checks if a bundle contains a certain item stack with a certain amount
   * @param possibleBundle The bundle to check
   * @param is The item stack to check for
   * @param amount The amount to check for
   * @return True if the bundle contains the item stack with the amount
   */
  static public boolean bundleContainsItemStack(@Nullable ItemStack possibleBundle, @Nullable ItemStack is, int amount) {
    if(possibleBundle == null || is == null) return false;
    if(possibleBundle.getType() != Material.BUNDLE) return false;
    if(!(possibleBundle.getItemMeta() instanceof BundleMeta im)) return false;
    for(ItemStack nestedItem : im.getItems()){
      if(nestedItem != null && nestedItem.getType().equals(is.getType()) && nestedItem.getAmount() >= amount) return true;
    }
    return false;
  }
  /**
   * This method checks if a shulker contains a certain item stack with a certain amount
   * @param possibleShulker The shulker to check
   * @param is The item stack to check for
   * @param amount The amount to check for
   * @return True if the shulker contains the item stack with the amount
   */
  static public boolean shulkerContainsItemStack(@Nullable ItemStack possibleShulker, @Nullable ItemStack is, int amount) {
    if(possibleShulker == null || is == null) return false;
    if(!(possibleShulker.getItemMeta() instanceof BlockStateMeta im)) return false;
    if(!(im.getBlockState() instanceof ShulkerBox shulker)) return false;
    // As bundles can be put in shulkers we need to check both the item and possible a bundle using bundleContainsItemStack()
    for(ItemStack nestedItem : shulker.getInventory().getContents()){
      if(nestedItem == null) continue;
      if(nestedItem.getType().equals(is.getType()) && nestedItem.getAmount() >= amount) return true;
      if(nestedItem.getType() == Material.BUNDLE && bundleContainsItemStack(nestedItem, is, amount)) return true;
    }
    return false;
  }
  /**
   * This method swaps the items in two slots of a player inventory
   * @param pi The player inventory to swap the items for
   * @param i1 The first slot [0-35]
   * @param i2 The second slot [0-35]
   */
  static public void swapItems(@Nullable PlayerInventory pi, int i1, int i2) {
    if(pi == null) return;
    if(i1 >= pi.getSize() || i2 >= pi.getSize()) return;
    ItemStack item1 = pi.getItem(i1);
    ItemStack item2 = pi.getItem(i2);
    pi.setItem(i1, item2);
    pi.setItem(i2, item1);
  }
  /**
   * This method swaps the items in a player inventory and a shulker box
   * @param pi The player inventory to swap the items for
   * @param i1 The slot in the player inventory [0-35]
   * @param shulkerBoxIndex The slot in the player inventory that contains the shulker box [0-35]
   * @param i2 The slot in the shulker box [0-26]
   */
  static public void swapItemsFromShulker(@Nullable PlayerInventory pi, int i1, int shulkerBoxIndex, int i2) {
    if(pi == null) return;
    if(i1 >= pi.getSize() || shulkerBoxIndex >= pi.getSize() || i2 >= 27) return;
    ItemStack item = pi.getItem(shulkerBoxIndex);
    if(item == null) return;
    if(!(item.getItemMeta() instanceof BlockStateMeta im)) return;
    if(!(im.getBlockState() instanceof ShulkerBox sb)) return;
    ItemStack item1 = pi.getItem(i1);
    ItemStack item2 = sb.getInventory().getItem(i2);
    pi.setItem(i1, item2);
    sb.getInventory().setItem(i2, item1);
    sb.update();
    im.setBlockState(sb);
    item.setItemMeta(im);
    pi.setItem(shulkerBoxIndex, item);
  }
}
