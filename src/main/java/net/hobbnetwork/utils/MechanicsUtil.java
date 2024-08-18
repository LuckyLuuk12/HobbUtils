package net.hobbnetwork.utils;

import net.hobbnetwork.custom.Triple;
import net.hobbnetwork.managers.HookManager;
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
   * Note that this method does not implement un-hiding on purpose, please use {@link #showEntityForAll(HookManager, Entity, Player...)} for that
   * @param toBeHidden The entity to be hidden
   * @param except The players that will still see the entity (optional)
   */
  static public void hideEntityForAll(HookManager hookManager, @Nullable Entity toBeHidden, Player... except) {
    if(!hookManager.isHooked()) return;
    if(toBeHidden == null) return;
    ArrayList<Player> exceptions = new ArrayList<>(List.of(except));
    for(Player p : hookManager.getPlugin().getServer().getOnlinePlayers()) {
      if(!exceptions.contains(p) ) p.hideEntity(hookManager.getPlugin(), toBeHidden);
    }
  }
  /**
   * This method shows an entity to all players except the ones specified
   * @param toBeUnhidden The entity to be shown
   * @param except The players that will not see the entity (optional)
   */
  static public void showEntityForAll(HookManager hookManager, @Nullable Entity toBeUnhidden, Player... except) {
    if(!hookManager.isHooked()) return;
    if(toBeUnhidden == null) return;
    ArrayList<Player> exceptions = new ArrayList<>(List.of(except));
    for(Player p : hookManager.getPlugin().getServer().getOnlinePlayers()) {
      if(!exceptions.contains(p) ) p.showEntity(hookManager.getPlugin(), toBeUnhidden);
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
   * @param checkBundles Whether to check for (nested in shulker boxes) bundles as well
   * @return True if the inventory contains the item stack with the amount
   */
  static public boolean inventoryContainsItemStack(@Nullable PlayerInventory pi, @Nullable  ItemStack is, int amount, boolean... checkBundles) {
    if(pi == null || is == null) return false;
    boolean checkBundlesFlag = checkBundles.length > 0 && checkBundles[0];
    for(ItemStack item : pi) {
      if(item == null) continue;
      if(item.getType().equals(is.getType()) && item.getAmount() >= amount
        || shulkerContainsItemStack(item, is, amount)
        || (checkBundlesFlag && bundleContainsItemStack(item, is, amount))) return true;
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
   * @param checkBundles Whether to check for bundles as well
   * @return True if the shulker contains the item stack with the amount
   */
  static public boolean shulkerContainsItemStack(@Nullable ItemStack possibleShulker, @Nullable ItemStack is, int amount, boolean... checkBundles) {
    if(possibleShulker == null || is == null) return false;
    boolean checkBundlesFlag = checkBundles.length > 0 && checkBundles[0];
    if(!(possibleShulker.getItemMeta() instanceof BlockStateMeta im)) return false;
    if(!(im.getBlockState() instanceof ShulkerBox shulker)) return false;
    // As bundles can be put in shulkers we need to check both the item and possible a bundle using bundleContainsItemStack()
    for(ItemStack nestedItem : shulker.getInventory().getContents()){
      if(nestedItem == null) continue;
      if(nestedItem.getType().equals(is.getType()) && nestedItem.getAmount() >= amount) return true;
      if(checkBundlesFlag && nestedItem.getType() == Material.BUNDLE && bundleContainsItemStack(nestedItem, is, amount)) return true;
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
    // Check for armor and offhand slots for i1 and i2 and get/set using appropriate methods
    // 40 = offhand, 36-39 = armor, 0-35 = main inventory
    i1 = Math.min(i1, 40);
    i2 = Math.min(i2, 40);
    if(i1 == 40) {
      ItemStack item1 = pi.getItemInOffHand();
      ItemStack item2 = pi.getItem(i2);
      pi.setItemInOffHand(item2);
      pi.setItem(i2, item1);
    } else if(i2 == 40) {
      ItemStack item1 = pi.getItem(i1);
      ItemStack item2 = pi.getItemInOffHand();
      pi.setItem(i1, item2);
      pi.setItemInOffHand(item1);
    } else {
      ItemStack item1 = pi.getItem(i1);
      ItemStack item2 = pi.getItem(i2);
      pi.setItem(i1, item2);
      pi.setItem(i2, item1);
    }
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
  /**
   * This method swaps the items in two shulker boxes
   * @param pi The player inventory to swap the items for
   * @param i1 The slot in the player inventory that contains the first shulker box [0-35]
   * @param si1 The slot in the first shulker box [0-26]
   * @param i2 The slot in the player inventory that contains the second shulker box [0-35]
   * @param si2 The slot in the second shulker box [0-26]
   */
  static public void swapItemsBetweenShulkers(@Nullable PlayerInventory pi, int i1, int si1, int i2, int si2) {
    if(pi == null) return;
    if(i1 >= pi.getSize() || i2 >= pi.getSize()) return;
    ItemStack item1 = pi.getItem(i1);
    ItemStack item2 = pi.getItem(i2);
    if(item1 == null || item2 == null) return;
    if(!(item1.getItemMeta() instanceof BlockStateMeta im1) || !(item2.getItemMeta() instanceof BlockStateMeta im2)) return;
    if(!(im1.getBlockState() instanceof ShulkerBox sb1) || !(im2.getBlockState() instanceof ShulkerBox sb2)) return;
    ItemStack nestedItem1 = sb1.getInventory().getItem(si1);
    ItemStack nestedItem2 = sb2.getInventory().getItem(si2);
    sb1.getInventory().setItem(si1, nestedItem2);
    sb2.getInventory().setItem(si2, nestedItem1);
    sb1.update();
    sb2.update();
    im1.setBlockState(sb1);
    im2.setBlockState(sb2);
    item1.setItemMeta(im1);
    item2.setItemMeta(im2);
    pi.setItem(i1, item1);
    pi.setItem(i2, item2);
  }

  /**
   * This method finds an {@link ItemStack} in a {@link PlayerInventory}.
   * @param pi The player inventory to search
   * @param i The item stack to find
   * @return A {@link Triple} containing the slot index, whether the item was found in a shulker box,
   *         and the slot index in the shulker box or null if the item was not found.<br>
   *         The shulker box slot index will be null if the item was not found in a shulker box.
   *         Or a triple with nulls if the item was not found.
   */
  @NotNull static public Triple<Integer, Boolean, Integer> find(@Nullable PlayerInventory pi, @Nullable ItemStack i) {
    if(pi == null || i == null) return new Triple<>(null, null, null);
    for(int j = 0; j < pi.getSize(); j++) {
      ItemStack item = pi.getItem(j);
      if(item == null) continue;
      if(item.getType().equals(i.getType()) && item.getAmount() >= i.getAmount()) return new Triple<>(j, false, null);
      if(item.getItemMeta() instanceof BlockStateMeta im) {
        if(im.getBlockState() instanceof ShulkerBox sb) {
          for(int k = 0; k < sb.getInventory().getSize(); k++) {
            ItemStack nestedItem = sb.getInventory().getItem(k);
            if(nestedItem == null) continue;
            if(nestedItem.getType().equals(i.getType()) && nestedItem.getAmount() >= i.getAmount()) return new Triple<>(j, true, k);
          }
        }
      }
    }
    return new Triple<>(null, null, null);
  }
  /**
   * This method swaps the items using two {@link Triple} objects. Where the first value is the slot index,
   * the second value is whether the item is in a shulker box, and the third value is the slot index in the shulker box.
   * @see #find(PlayerInventory, ItemStack)
   * @param pi The player inventory to swap the items for
   * @param item1 The first item to swap
   * @param item2 The second item to swap
   */
  static public void swapItems(
    @Nullable PlayerInventory pi,
    Triple<Integer, Boolean, Integer> item1,
    Triple<Integer, Boolean, Integer> item2) {
    if(pi == null || item1 == null || item2 == null) return;
    Integer i11 = item1.fst(); // First Item, First Slot
    Boolean inShulker1 = item1.snd(); // First Item, Second Slot
    Integer i13 = item1.thd(); // First Item, Shulker Slot
    Integer i21 = item2.fst(); // Second Item, First Slot
    Boolean inShulker2 = item2.snd(); // Second Item, Second Slot
    Integer i23 = item2.thd(); // Second Item, Shulker Slot
    if(i11 == null || i21 == null || inShulker1 == null || inShulker2 == null) return;
    if(!inShulker1 && !inShulker2) { // Both items are in the player inventory, NOT in a shulker box
      swapItems(pi, i11, i21);
    } else if(inShulker1 && inShulker2 && i13 != null && i23 != null) { // Both items are in a shulker box
      swapItemsBetweenShulkers(pi, i11, i13, i21, i23);
    } else if(inShulker1 && i13 != null) { // First item is in a shulker box and the second item is in the player inventory
      swapItemsFromShulker(pi, i21, i11, i13);
    } else if(i23 != null) { // First item is in the player inventory and the second item is in a shulker box
      swapItemsFromShulker(pi, i11, i21, i23);
    }
  }
}
