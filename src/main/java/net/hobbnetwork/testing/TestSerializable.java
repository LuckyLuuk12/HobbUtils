package net.hobbnetwork.testing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.hobbnetwork.custom.HobbLocation;
import net.hobbnetwork.custom.HobbWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class is used to test the serialization of objects.
 * It contains a few fields that are used to test the serialization of different types of objects.
 * Purely aimed to be compatible with the HobbStorage class.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestSerializable implements Serializable {
  private int id = 0;
  private String name = "Test";
  private boolean isTest = true;
  private HobbWorld world = new HobbWorld(Bukkit.getWorld("world"));
  private HobbLocation location = new HobbLocation(Bukkit.getWorld("world"), 0, 0, 0);
  private Player owner = Bukkit.getPlayer("LuckyLuuk_");
  private ArrayList<UUID> members = new ArrayList<>();
  private HashMap<String, HobbLocation> warps = new HashMap<>();
  private ArrayList<TestSerializable> children = new ArrayList<>();
}
