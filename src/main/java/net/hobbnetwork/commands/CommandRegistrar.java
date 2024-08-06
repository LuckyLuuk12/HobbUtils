package net.hobbnetwork.commands;

import net.hobbnetwork.managers.HookManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.ApiStatus;

/**
 * TODO: This class should enable the {@link HobbCommand} to register itself and its subcommands to the server.
 */
@ApiStatus.Experimental
public class CommandRegistrar {

  private final HookManager hookManager;

  public CommandRegistrar(HookManager hookManager) {
    this.hookManager = hookManager;
  }

  public void registerCommand(String name, HobbCommand executor) {
    Bukkit.getCommandMap().register(name, executor);
    PluginCommand command = Bukkit.getPluginCommand(name);
    if(command == null) return;
    command.setExecutor(executor);
    command.setTabCompleter(executor);
  }
}
