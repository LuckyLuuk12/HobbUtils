package net.hobb.utils;

import net.hobb.HobbUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A utility class for creating GUIs
 */
public class GUIUtils implements Listener {

  private final HashMap<Inventory, HobbGUI> guiMap = new HashMap<>();
  /**
   * Creates a new GUIUtils object and registers it as a listener
   */
  public GUIUtils() {
    JavaPlugin plugin = HobbUtils.isNotHooked() ? HobbUtils.getThisPlugin() : HobbUtils.getHookedPlugin();
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  /**
   * A class representing a GUI, thus containing all the necessary information to create a GUI
   */
  public class HobbGUI {
    private Inventory inventory;
    private final InventoryType type;
    private final Component title;
    private final Integer size;
    private final ArrayList<Boolean> isEditable;
    private final ArrayList<ClickCallback> callbacks;

    /**
     * Creates a new HobbGUI object
     * @param type The {@link InventoryType type} of the inventory
     * @param title The title of the inventory
     * @param size The size of the inventory (if applicable), otherwise you <b>MUST</b> provide null
     */
    public HobbGUI(InventoryType type, Component title, Integer size) {
      this.type = type;
      this.title = title;
      this.size = size;
      this.isEditable = new ArrayList<>(size);
      this.callbacks = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
        this.isEditable.add(false);
        this.callbacks.add(null);
      }
    }

    public void setItem(int slot, ItemStack item, boolean isEditable, ClickCallback callback) {
      if (size != null && slot >= size) return;
      inventory = inventory == null ? Bukkit.createInventory(null, type, title) : inventory;
      inventory.setItem(slot, item);
      this.isEditable.set(slot, isEditable);
      this.callbacks.set(slot, callback);
      guiMap.put(inventory, this);
    }

    public void open(Player player) {
      player.openInventory(this.inventory);
    }

    public ClickCallback getCallback(int slot) {
      return this.callbacks.get(slot);
    }
  }

  @FunctionalInterface
  public interface ClickCallback {
    void onClick(InventoryClickEvent e);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    Inventory inventory = event.getInventory();
    if (guiMap.containsKey(inventory)) {
      HobbGUI gui = guiMap.get(inventory);
      int slot = event.getRawSlot();
      if (slot < gui.size) {
        ClickCallback callback = gui.getCallback(slot);
        if (callback != null) {
          callback.onClick(event);
        }
        if (!gui.isEditable.get(slot)) {
          event.setCancelled(true);
        }
      }
    }
  }
}