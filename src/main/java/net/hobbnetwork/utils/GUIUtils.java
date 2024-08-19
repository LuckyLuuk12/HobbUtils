package net.hobbnetwork.utils;

import net.hobbnetwork.managers.HookManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class GUIUtils implements Listener {
  private final HookManager hookManager;
  private static final HashMap<Inventory, GUIUtils> guiMap = new HashMap<>();
  private Inventory inventory;
  private final InventoryType type;
  private final Component title;
  private final Integer size;
  private final ArrayList<Boolean> isEditable;
  private final ArrayList<ClickCallback> callbacks;

  /**
   * This constructor is used to create a GUIUtils object with a title that is a Component object.
   * @param hookManager The HookManager object to use
   * @param type The InventoryType of the GUI
   * @param title The title of the GUI
   * @param size The size of the GUI
   */
  public GUIUtils(HookManager hookManager, InventoryType type, Component title, Integer size) {
    this.hookManager = hookManager;
    this.type = type;
    this.title = title;
    this.size = size;
    this.isEditable = new ArrayList<>(size);
    this.callbacks = new ArrayList<>(size);
    init();
  }

  /**
   * This constructor is used to create a GUIUtils object with a title that is a string.
   * This string will be parsed into a Component object, using {@link TextUtil#parseMcString(String)}.
   * @param hookManager The HookManager object to use
   * @param type The InventoryType of the GUI
   * @param title The title of the GUI
   * @param size The size of the GUI
   */
  public GUIUtils(HookManager hookManager, InventoryType type, String title, Integer size) {
    this(hookManager, type, TextUtil.parseMcString(title), size);
  }
  /**
   * This method ensures that the isEditable and callbacks ArrayLists are initialized.
   * It also registers the GUIUtils object as a listener if the HookManager is hooked.
   */
  private void init() {
    for (int i = 0; i < size; i++) {
      this.isEditable.add(false);
      this.callbacks.add(null);
    }
    if (hookManager.isHooked()) {
      Bukkit.getPluginManager().registerEvents(this, hookManager.getPlugin());
    }
  }
  /**
   * This method sets the item in the GUI at the specified slot.
   * @param slot The slot to set the item in
   * @param item The ItemStack to set
   * @param isEditable Whether the item is editable
   * @param callback The ClickCallback to use
   */
  public void setItem(int slot, ItemStack item, boolean isEditable, ClickCallback callback) {
    if (size != null && slot >= size) return;
    if (inventory == null) {
      if (type == InventoryType.CHEST && size != null) {
        inventory = Bukkit.createInventory(null, (size/9)*9, title);
      } else {
        inventory = Bukkit.createInventory(null, type, title);
      }
    }
    inventory.setItem(slot, item);
    this.isEditable.set(slot, isEditable);
    this.callbacks.set(slot, callback);
    guiMap.put(inventory, this);
  }
  /**
   * This method opens the GUI for the specified player.<br>
   * In case a CHEST InventoryType is used, the size should be a multiple of 9.
   * and this method will create a new Inventory object with the specified size.
   * @param player The player to open the GUI for
   */
  public void open(Player player) {
    if (inventory == null) return;
    player.openInventory(this.inventory);
  }
  /**
   * This method returns the ClickCallback for the specified slot.
   * @param slot The slot to get the ClickCallback for
   * @return The ClickCallback for the specified slot
   */
  public ClickCallback getCallback(int slot) {
    return this.callbacks.get(slot);
  }
  /**
   * This method is used to handle InventoryClickEvents.
   * @param event The InventoryClickEvent to handle
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    Inventory inventory = event.getInventory();
    if (!guiMap.containsKey(inventory)) return;
    GUIUtils gui = guiMap.get(inventory);
    int slot = event.getRawSlot();
    if (slot < 0 || slot >= gui.size) return;
    event.setCancelled(gui.isEditable.get(slot) == null || !gui.isEditable.get(slot));
    ClickCallback callback = gui.getCallback(slot);
    if (callback == null) return;
    callback.onClick(event);
  }
  /**
   * This interface is used to handle InventoryClickEvents.
   */
  @FunctionalInterface
  public interface ClickCallback {
    void onClick(InventoryClickEvent e);
  }
}