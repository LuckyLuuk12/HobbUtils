package net.hobbnetwork.commands;

import net.hobbnetwork.managers.HookManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * This class is used to register commands to the server.
 * It uses reflection to register commands.
 * If the registration fails, it will fall back to the default Bukkit command registration.
 */
public class CommandRegistrar {
  private final HookManager hookManager;

  public CommandRegistrar(HookManager hookManager) {
    this.hookManager = hookManager;
  }
  /**
   * This method registers a command to the server. <br>
   * It first tries to register the command the normal way, if that fails it will fall back to using reflection.<br>
   * This reflection is done by {@link CommandRegistrar#attemptRegistration(String, HobbCommand)}.
   * @param name The name of the command
   * @param executor The executor of the command
   * @throws RuntimeException If the command could not be registered because the command was not in the plugin.yml
   */
  public void registerCommand(String name, HobbCommand executor) throws Exception {
    try {
      PluginCommand command = Bukkit.getPluginCommand(name);
      if(command == null) throw new RuntimeException("Could not register command: " + name);
      command.setExecutor(executor);
      command.setTabCompleter(executor);
    } catch (Exception e) {
      attemptRegistration(name, executor);
    }
  }
  /**
   * This method attempts to register a command using reflection.
   * @param name The name of the command
   * @param executor The executor of the command
   * @throws Exception If the registration fails
   */
  private void attemptRegistration(String name, HobbCommand executor) throws Exception {
    // Get the CommandMap
    Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
    commandMapField.setAccessible(true);
    CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
    // Create a new PluginCommand
    Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
    constructor.setAccessible(true);
    PluginCommand command = constructor.newInstance(name, hookManager.getPlugin());
    // Set the executor and tab completer
    command.setExecutor(executor);
    command.setTabCompleter(executor);
    // Register the command
    commandMap.register(hookManager.getPlugin().getName(), command);
  }
}
