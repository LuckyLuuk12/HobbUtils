package net.hobbnetwork.custom;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;

/**
 * A custom location class that is serializable using {@link HobbWorld} instead of {@link World}
 */
public class HobbLocation extends Location implements Serializable {
  private final HobbWorld world;

  public HobbLocation(World world, double x, double y, double z, float yaw, float pitch) {
    super(world, x, y, z, yaw, pitch);
    this.world = new HobbWorld(world);
  }
  public HobbLocation(World world, double x, double y, double z) {
    super(world, x, y, z);
    this.world = new HobbWorld(world);
  }
  public HobbLocation(Location loc) {
    super(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    this.world = new HobbWorld(loc.getWorld());
  }
}
