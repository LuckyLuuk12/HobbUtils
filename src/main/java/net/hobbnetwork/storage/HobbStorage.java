package net.hobbnetwork.storage;

import lombok.Getter;
import net.hobbnetwork.HobbUtils;

import java.util.ArrayList;

/**
 * This class is a wrapper for the different types of storage
 */
public class HobbStorage {
  @Getter
  private Storage storage;
  private String name;
  private String path;

  /**
   * This constructor creates a new Storage object
   * It will fall back to YML if the type is not supported or if it cannot initialize
   * @param type The {@link StorageType type} of storage to use
   * @param options The options to use for the storage <br>
   *                index 0: The name of the table/yml file
   *                index 1: The path to the file (if needed)
   */
  public HobbStorage(StorageType type, String... options) {
    if(HobbUtils.isNotHooked()) {
      HobbUtils.getThisPlugin().getLogger().warning("HobbStorage is not hooked!");
      return;
    }
    this.name = options.length > 0 ? options[0] : "key_value";
    this.path = options.length > 1 ? options[1] : null;
    if(type == StorageType.H2) {
      storage = new H2Storage();
    } else if(type == StorageType.YML) {
      storage = new YMLStorage(this.name, this.path);
    } else {
      storage = new YMLStorage(this.name, this.path);
    }
    boolean canInit = storage.init(this.name).join();
    if(canInit) return;
    HobbUtils.getThisPlugin().getLogger().warning("Could not initialize storage, falling back to YML");
    storage = new YMLStorage(this.name, this.path);
    boolean fallBackInit = storage.init(this.name).join();
    if(fallBackInit) return;
    HobbUtils.getThisPlugin().getLogger().severe("Could not initialize fallback storage, disabling HobbStorage");
  }

  /**
   * This method makes a backup of the current {@link #storage} to a YML file
   * If the storage is already a YMLStorage, it will just save it
   * @return Whether the backup was successful for all keys in the {@link TypedKeyValue#ALL} list
   */
  public boolean makeYMLBackUp() {
    ArrayList<Boolean> success = new ArrayList<>(TypedKeyValue.ALL.size());
    if(storage instanceof YMLStorage ymlStorage) {
      return ymlStorage.save();
    } else {
      YMLStorage ymlStorage = new YMLStorage(this.name, this.path);
      TypedKeyValue.ALL.forEach((tkv) -> this.storage.getValue(tkv).thenAccept((value) -> ymlStorage.setValue(tkv, value).thenAccept(success::add)));
    }
    return success.stream().allMatch((b) -> b);
  }

  public enum StorageType {
    YML,
    H2,
    MYSQL,
    SQLITE
  }
}
