package net.hobbnetwork.testing;

import net.hobbnetwork.HobbUtils;
import net.hobbnetwork.commands.HobbCommand;
import net.hobbnetwork.custom.HobbLocation;
import net.hobbnetwork.custom.HobbWorld;
import net.hobbnetwork.storage.HobbStorage;
import net.hobbnetwork.storage.TypedKeyValue;
import net.hobbnetwork.utils.GUIUtils;
import net.hobbnetwork.utils.ItemUtil;
import net.hobbnetwork.utils.LogUtil;
import net.hobbnetwork.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
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
    this.getSubCommands().add(new StoreCustomClass());
    this.getSubCommands().add(new LoadCustomClass());
    this.getSubCommands().add(new Log());
    this.getSubCommands().add(new GUI());
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
  private class StoreCustomClass extends HobbCommand {
    public StoreCustomClass() {
      this.subLevel = 1;
      this.name = "store-custom-class";
      this.description = "Store a custom class";
      this.permission = "hobb.utils.test.store-custom-class";
    }
    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      // Store a custom class
      if(storage == null) {
        sender.sendMessage("Database not initialized yet! Try running /test init-db first.");
        return;
      }
      // Create an instance of TestSerializable and use setValue() to store it:
      TestSerializable test = new TestSerializable();
      // Change some values to test the serialization:
      test.getWarps().put("spawn", new HobbLocation(HobbUtils.getHookManager().getPlugin().getServer().getWorlds().get(0), 1, 80, 2));
      test.setWorld(new HobbWorld(HobbUtils.getHookManager().getPlugin().getServer().getWorlds().get(0)));

      // Create a key like "test_2021-09-01.18:00" to store the object:
      String dateKey = "test_"+ TextUtil.formatDate(System.currentTimeMillis(), "dd-MM-yyyy_HH:mm");
      TypedKeyValue<TestSerializable> testTKV = new TypedKeyValue<>(dateKey, TestSerializable.class, () -> test);
      storage.setValue(testTKV, test).thenAccept(success -> HobbUtils.getConsole().log(LogUtil.LogLevel.TEST, "TestSerializable Stored: "+success));
      sender.sendMessage("Stored TestSerializable with key: "+dateKey+" You can load it using /test load-custom-class "+dateKey);
    }
  }
  private class LoadCustomClass extends HobbCommand {
    public LoadCustomClass() {
      this.subLevel = 1;
      this.name = "load-custom-class";
      this.description = "Load a custom class";
      this.permission = "hobb.utils.test.load-custom-class";
    }
    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      // Load a custom class
      if(storage == null) {
        sender.sendMessage("Database not initialized yet! Try running /test init-db first.");
        return;
      }
      if(args.length == 0) {
        sender.sendMessage("You must provide a key to load the custom class!");
        return;
      }
      // Create an instance of TestSerializable and use getValue() to load it:
      TypedKeyValue<TestSerializable> testTKV = new TypedKeyValue<>(args[0], TestSerializable.class, () -> new TestSerializable());
      storage.getValue(testTKV).thenAccept(value -> HobbUtils.getConsole().log(LogUtil.LogLevel.TEST, "TestSerializable Loaded: "+value));
    }
  }

  private class Log extends HobbCommand {
    public Log() {
      this.subLevel = 1;
      this.name = "log";
      this.description = "Log a message";
      this.permission = "hobb.utils.test.log";
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
}
