package net.hobbnetwork.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Safeguards implements Listener {

  /**
   * This class is responsible for preventing certain actions that could be harmful to the server
   * @param pl The plugin that is using this class
   */
  public Safeguards(JavaPlugin pl) {
    pl.getServer().getPluginManager().registerEvents(this, pl);
  }

  /**
   * This specific event handler prevents any {@link org.bukkit.entity.Player} from using /reload confirm
   */
  @EventHandler
  public void preventServerReloadByPlayer(PlayerCommandPreprocessEvent e) {
    e.setCancelled(e.getMessage().equalsIgnoreCase("/reload confirm"));
  }
  /**
   * This specific event handler prevents any <b>non-</b>{@link org.bukkit.entity.Player} from using /reload confirm
   */
  @EventHandler
  public void preventServerReloadByConsole(ServerCommandEvent e) {
    e.setCancelled(e.getCommand().equalsIgnoreCase("reload confirm"));
  }
}
