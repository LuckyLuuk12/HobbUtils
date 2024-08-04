package net.hobb.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GUIUtils {

  /**
   * This class is a utility class for creating GUIs
   * It internally contains all the fields and methods needed to create a GUI
   */
  public class HobbGUI {
    private InventoryType type;
    private Component title;
    private int size;
    private ArrayList<Boolean> isEditable;

    public HobbGUI(InventoryType type, Component title, int size) {
      this.type = type;
      this.title = title;
      this.size = size;
      this.isEditable = new ArrayList<>(size);
    }

    /**
     * This method sets the item in a slot, and whether it is editable
     * It also provides a callback for when the item is clicked.
     * @param slot The slot to set the item in [0, size)
     * @param item The {@link ItemStack} to set
     * @param isEditable Whether the item is editable
     */
    public void setItem(int slot, ItemStack item, boolean isEditable, ClickCallback callback) {
      if(slot >= size) return;
      this.isEditable.set(slot, isEditable);

    }
  }

  @FunctionalInterface
  public interface ClickCallback {
    void onClick(InventoryClickEvent e);
  }
}
