package net.hobbnetwork.custom;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.*;

/**
 * A custom location class that is serializable using {@link HobbWorld} instead of {@link World}
 */
public class HobbLocation extends Location implements Serializable {
  private final HobbWorld world;

  public HobbLocation() {
    super(null, 0, 0, 0);
    this.world = new HobbWorld(null);
  }
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

  @Serial
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeObject(new HobbWorld(getWorld()));
    out.writeDouble(getX());
    out.writeDouble(getY());
    out.writeDouble(getZ());
    out.writeFloat(getYaw());
    out.writeFloat(getPitch());
  }

  @Serial
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    HobbWorld world = (HobbWorld) in.readObject();
    double x = in.readDouble();
    double y = in.readDouble();
    double z = in.readDouble();
    float yaw = in.readFloat();
    float pitch = in.readFloat();
    setWorld(world.getWorld());
    setX(x);
    setY(y);
    setZ(z);
    setYaw(yaw);
    setPitch(pitch);
  }
}
