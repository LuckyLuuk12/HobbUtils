package net.hobbnetwork.commands;

import lombok.Getter;
import lombok.Setter;
import net.hobbnetwork.managers.HookManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Most of this class is copied from the TippieUtils plugin by Tippie. The original source code can be found at
 * <a href="https://github.com/rowan-vr/TippieUtils/">TippieUtils</a>.
 */
@Getter
public class HobbCommand extends Command implements TabExecutor {
  /**
   * The registered subcommands of this command.
   */
  @Getter private final List<HobbCommand> subCommands = new ArrayList<>();
  /**
   * The name of this command.
   */
  protected String name = null;
  /**
   * The description of this command used in generated help messages.
   * @see HobbCommand#sendHelpMessage(CommandSender, String, String)
   */
  protected String description = "";
  /**
   * The permission needed to execute this command
   */
  protected String permission = null;
  /**
   * The sublevel of this command. This indicates how deep the command is within subcommands.
   * For example, if this command is the root command, then this value is 0.
   * If this command is a subcommand of another command, then this value is 1.
   */
  protected int subLevel = 0;
  /**
   * The prefix used in the help message.
   * @see HobbCommand#sendHelpMessage(CommandSender, String, String)
   */
  protected String prefix = "";
  /**
   * Whether the command can register itself to the server.
   * This is set to false by default.
   * @see HobbCommand#register(HookManager, boolean...)
   */
  protected boolean canRegister = false;

  protected HobbCommand(@NotNull String name) {
    super(name);
  }
  @Override
  public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
    return onCommand(sender, this, commandLabel, args);
  }

  /**
   * @hidden
   */
  @Override @ApiStatus.Internal
  public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!hasPermission(sender, permission)) {
      sender.sendMessage("§cYou need `§4" + permission + "§c` to execute this command.");
      return true;
    }
    if (subCommands.isEmpty()) {
      try {
        Method executes = this.getClass().getMethod("executes", CommandSender.class, Command.class, String.class, String[].class);
        this.executes(sender, command, (subLevel == 0 ) ? label : label + " " + name, Arrays.copyOfRange(args,subLevel == 0 ? 0 : 1,args.length));
      } catch (NoSuchMethodException ignored) {
        sender.sendMessage("Command does not implement executes(). Type /help for help.");
      }
    } else {
      if (args.length < subLevel + 1) {
        sendHelpMessage(sender, ((subLevel == 0) ? "" : label + " ") + name, prefix);
      } else {
        for (HobbCommand subCommand : subCommands) {
          if (subCommand.name.equalsIgnoreCase(args[subLevel])) {
            try {
              Method executes = subCommand.getClass().getMethod("executes", CommandSender.class, Command.class, String.class, String[].class);
              subCommand.onCommand(sender, command, ((subLevel == 0) ? "" : label + " ") + name, Arrays.copyOfRange(args,subLevel == 0 ? 0 : 1,args.length)); //TODO: Test this
            } catch (NoSuchMethodException ignored) {
              sender.sendMessage("Command does not implement executes(). Type /help for help.");
            }
            return true;
          }
        }
        sendHelpMessage(sender, ((subLevel == 0) ? "" : label + " ") + name, prefix);
      }
    }
    return true;
  }

  /**
   * @hidden
   */
  @Nullable @Override @ApiStatus.Internal
  public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
    List<String> complete = null;
    if (subCommands.isEmpty()) {
      complete = completes(sender, command, alias + " " + name, args);
    } else if (args.length >= 2) {
      for (HobbCommand subCommand : subCommands) {
        if (subCommand.name.equalsIgnoreCase(args[0])) {
          if (subCommand.permission != null && !sender.hasPermission(subCommand.permission))
            complete = new ArrayList<>();
          else
            complete = subCommand.onTabComplete(sender, command, ((subLevel == 0) ? "" : alias + " ") + name, Arrays.copyOfRange(args, 1, args.length));
          break;
        }
      }
    } else {
      complete = subCommands.stream().filter(cmd -> cmd.permission == null || sender.hasPermission(cmd.permission)).map(cmd -> cmd.name).collect(Collectors.toList());
    }
    if (complete == null) return null;
    return complete.stream().filter(str -> str.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());

  }

  /**
   * Called when the command is tab completed.
   * @param sender The sender of the command.
   * @param command The command.
   * @param alias The alias of the command. This is the same as the {@link #name} of the command.
   * @param args The arguments of the command. This does not include the subcommand.Example the command '/test subcommand argument' would only have 'argument' here
   * @return The list of completions
   */
  public List<String> completes(CommandSender sender, Command command, String alias, String[] args) {
    return new ArrayList<>();
  }

  /**
   * Called when the command is tab executed.
   * @param sender The sender of the command.
   * @param command The command.
   * @param label The alias of the command. This is the same as the {@link #name} of the command.
   * @param args The arguments of the command. This does not include the subcommand.<br>
   *             Example: the command '/test subcommand argument' would only have 'argument' here
   */
  public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
    throw new NoSuchMethodException("Command does not implement executes()");
  }

  /**
   * Sends a help message about the subcommands of this command when called.
   * @param sender The command sender to send the help message to.
   * @param label The label of the command.
   * @param prefix The prefix used in the help message.
   */
  protected void sendHelpMessage(CommandSender sender, String label, String prefix) {
    sender.sendMessage((prefix.isEmpty() ? "" : prefix + " ")  + "§6" + label.replaceFirst(String.valueOf(label.charAt(0)),String.valueOf(label.charAt(0)).toUpperCase()) + " help");
    sender.sendMessage("");
    for (HobbCommand subCommand : subCommands) {
      if (subCommand.permission == null || sender.hasPermission(subCommand.permission)) sender.sendMessage("§7 - §f/" + label + " " + subCommand.name + "§7: " + subCommand.description);
    }
    sender.sendMessage("");
  }

  /**
   * Registers this command and all subcommands to the server.
   * <br><b>YOU SHOULD CALL THIS IN THE {@link JavaPlugin#onEnable()} method!</b>
   * @param hookManager The {@link HookManager} to use for registering the command.
   * @param deep Whether to register subcommands as well.
   */
  public void register(HookManager hookManager, boolean... deep) throws NullPointerException {
    boolean deep1 = deep.length > 0 && deep[0];
    PluginCommand command = Bukkit.getPluginCommand(name);
    if(command == null) throw new NullPointerException("Command " + name + " is not registered in the plugin.yml");
    if(canRegister) command.setExecutor(this);
    if(canRegister) command.setTabCompleter(this);
    if(!deep1) return;
    try {
      for(HobbCommand subCommand : subCommands) {
        subCommand.register(hookManager, true);
      }
    } catch (Exception e) {
      hookManager.log(Level.SEVERE, "Could not register subcommands for " + name, "Use this register method on an HobbCommand instance instead!");
    }
  }

  /**
   * This method checks if the sender has the permission to execute the command.
   * It also checks for wildcard permissions.
   * An example of a wildcard permission is 'hobb.*' which would allow the sender to execute any command with the 'hobb.' prefix.
   * It also works "backwards" meaning that if the sender has 'hobb.command.other' they can execute 'hobb.command' as well.
   * @param sender The sender of the command
   * @param permission The permission to check for
   * @return Whether the sender has the permission
   */
  private boolean hasPermission(CommandSender sender, String permission) {
    if (permission == null) return true;
    if(sender.isOp()) return true;
    if (sender.hasPermission(permission)) return true;
    if (permission.endsWith(".*")) {
      String[] split = permission.split("\\.");
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < split.length - 1; i++) {
        builder.append(split[i]).append(".");
        if (sender.hasPermission(builder.toString())) return true;
      }
    }
    return false;
  }
}
