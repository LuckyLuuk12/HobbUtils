package net.hobbnetwork.custom;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;

public class HobbWorld implements Serializable {
  private final String worldName;

  public HobbWorld(@Nullable World world) {
    this.worldName = world == null ? "" : world.getName();
  }

  @Nullable
  public World getWorld() {
    return Bukkit.getWorld(worldName);
  }

  @Serial
  private Object readResolve() throws ObjectStreamException {
    return getWorld();
  }
}
