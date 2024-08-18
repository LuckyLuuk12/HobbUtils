package net.hobbnetwork.testing;

import net.hobbnetwork.HobbUtils;
import net.hobbnetwork.commands.HobbCommand;
import net.hobbnetwork.storage.HobbStorage;
import net.hobbnetwork.utils.GUIUtils;
import net.hobbnetwork.utils.ItemUtil;
import net.hobbnetwork.utils.LogUtil;
import net.hobbnetwork.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class TestCommand extends HobbCommand {
  private HobbStorage storage;
  public TestCommand() {
    this.subLevel = 0;
    this.name = "test";
    this.description = "Test command";
    this.permission = "hobb.utils.test";
    this.canRegister = true;
    this.getSubCommands().add(new InitDB());
    this.getSubCommands().add(new Log());
    this.getSubCommands().add(new GUI());
    this.getSubCommands().add(new CheckSlotIndices());
  }

  private class InitDB extends HobbCommand {
    public InitDB() {
      this.subLevel = 1;
      this.name = "init-db";
      this.description = "Initialize the database";
      this.permission = "hobb.utils.test.init-db";
    }
    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      // Initialize the database
      storage = new HobbStorage(HobbUtils.getHookManager(), HobbStorage.StorageType.H2);
      sender.sendMessage("Database initialized! You can now store and load custom classes using /test store-custom-class and /test load-custom-class");
      HobbUtils.getConsole().log(LogUtil.LogLevel.TEST, "Database initialized by test command!");
    }
  }

  private class Log extends HobbCommand {
    public Log() {
      this.subLevel = 1;
      this.name = "log";
      this.description = "Log a message";
      this.permission = "hobb.utils.test.log";
      this.canRegister = true;
    }
    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      StringBuilder message = new StringBuilder();
      for(String arg : args) {
        message.append(arg).append(" ");
      }
      // Log the message to console using the HookManager in all Level types:
      HobbUtils.getHookManager().log(Level.INFO, message.toString());
      HobbUtils.getHookManager().log(Level.WARNING, message.toString());
      HobbUtils.getHookManager().log(Level.SEVERE, message.toString());
      HobbUtils.getHookManager().log(Level.CONFIG, message.toString());
      HobbUtils.getHookManager().log(Level.FINE, message.toString());
      HobbUtils.getHookManager().log(Level.FINER, message.toString());
      HobbUtils.getHookManager().log(Level.FINEST, message.toString());
      HobbUtils.getHookManager().log(Level.ALL, message.toString());
      HobbUtils.getHookManager().log(Level.OFF, message.toString());
      // Log the message to console using the HookManager in custom LogLevel types:
      HobbUtils.getHookManager().log(LogUtil.LogLevel.BEST, message.toString());
      HobbUtils.getHookManager().log(LogUtil.LogLevel.BETTER, message.toString());
      HobbUtils.getHookManager().log(LogUtil.LogLevel.GOOD, message.toString());
      HobbUtils.getHookManager().log(LogUtil.LogLevel.DEBUG, message.toString());
      HobbUtils.getHookManager().log(LogUtil.LogLevel.TEST, message.toString());
      HobbUtils.getHookManager().log(LogUtil.LogLevel.TEXT, message.toString());
      HobbUtils.getHookManager().log(LogUtil.LogLevel.WARN, message.toString());
      HobbUtils.getHookManager().log(LogUtil.LogLevel.ERROR, message.toString());
      HobbUtils.getHookManager().log(LogUtil.LogLevel.CRASH, message.toString());
    }
  }

  private class GUI extends HobbCommand {
    public GUI() {
      this.subLevel = 1;
      this.name = "gui";
      this.description = "Open a GUI";
      this.permission = "hobb.utils.test.gui";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      if(!(sender instanceof Player p)) {
        sender.sendMessage("You must be a player to use this command!");
        return;
      }
      // Open a GUI for the player
      GUIUtils gui = new GUIUtils(HobbUtils.getHookManager(), InventoryType.CHEST, "Test GUI", 9);
      ItemStack head = ItemUtil.getItemStack(Material.PLAYER_HEAD, "Who is it?", "Itsa me, "+p.getName()+"!");
      gui.setItem(3, head, false, (clickEvent) -> {
        HobbUtils.getConsole().log(LogUtil.LogLevel.TEST, "Is cancelled: "+clickEvent.isCancelled());
        clickEvent.setCancelled(true); // Try to remove this and check whether it is protected by isEditable
        HobbUtils.getConsole().log(LogUtil.LogLevel.TEST, "Manually cancelled: "+clickEvent.isCancelled());
      });
      if(head.lore() != null) head.lore().add(TextUtil.parseMcString("You can take this one (:"));
      gui.setItem(5, head, true, (clickEvent) -> {
        HobbUtils.getConsole().log(LogUtil.LogLevel.TEST, "Is cancelled: "+clickEvent.isCancelled());
        clickEvent.setCancelled(true); // Try to remove this and check whether it is protected by isEditable
        HobbUtils.getConsole().log(LogUtil.LogLevel.TEST, "Manually cancelled: "+clickEvent.isCancelled());
      });
      gui.open(p);
    }
  }

  private class CheckSlotIndices extends HobbCommand {
    public CheckSlotIndices() {
      this.subLevel = 1;
      this.name = "check-slot-indices";
      this.description = "Fills you or the inventory holder you're looking at with named items representing to slot index";
      this.permission = "hobb.utils.test.check-slot-indices";
    }
    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      // Check slot indices
      if(!(sender instanceof Player p)) {
        sender.sendMessage("You must be a player to use this command!");
        return;
      }
      Block block = p.getTargetBlock(null, 5);
      InventoryHolder ih = (block instanceof BlockInventoryHolder bih) ? bih : p;
      for(int i = 0; i < ih.getInventory().getSize(); i++) {
        ItemStack item = ItemUtil.getItemStack(Material.PAPER, i+"");
        ih.getInventory().setItem(i, item);
      }
    }
  }
}
