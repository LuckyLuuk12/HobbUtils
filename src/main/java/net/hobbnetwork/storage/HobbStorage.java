package net.hobbnetwork.storage;

import lombok.Getter;
import net.hobbnetwork.listeners.Safeguards;
import net.hobbnetwork.managers.HookManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * This class is a wrapper for the different types of storage
 */
@Getter
public class HobbStorage {
  private boolean useInMemory = true;
  private final HashMap<TypedKeyValue<?>, Object> data = new HashMap<>();
  private final HookManager hookManager;
  private Storage storage;
  private String name;
  private String path;

  /**
   * This constructor creates a new Storage object
   * It will fall back to YML if the type is not supported or if it cannot initialize
   * @param type The {@link StorageType type} of storage to use
   * @param options The options to use for the storage <br>
   *                index 0: The name of the table/yml file<br>
   *                index 1: The path to the file (if needed)<br>
   *                index 2: "true" disables the in-memory storage (this is slower)<br>
   */
  public HobbStorage(HookManager hookManager, StorageType type, String... options) {
    this.hookManager = hookManager;
    if(!hookManager.isHooked()) {
      hookManager.log(Level.SEVERE, "HobbStorage is not hooked!");
      return;
    }
    new Safeguards(hookManager.getPlugin());
    this.name = options.length > 0 ? options[0] : "hobb-storage";
    this.path = options.length > 1 ? options[1] : null;
    this.useInMemory = options.length > 2 && options[2].equalsIgnoreCase("true");
    if(type == StorageType.H2) {
      storage = new H2Storage(hookManager);
    } else if(type == StorageType.YML) {
      storage = new YMLStorage(hookManager, this.name, this.path);
    } else {
      storage = new YMLStorage(hookManager, this.name, this.path);
    }
    boolean canInit = storage.init(this.name).join();
    if(canInit) return;
    hookManager.log(Level.SEVERE,"[HobbStorage] Could not initialize storage, falling back to YML");
    storage = new YMLStorage(hookManager, this.name, this.path);
    boolean fallBackInit = storage.init(this.name).join();
    if(fallBackInit) return;
    hookManager.log(Level.SEVERE,"[HobbStorage] Could not initialize fallback storage, disabling HobbStorage");
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
      YMLStorage ymlStorage = new YMLStorage(hookManager, this.name, this.path);
      TypedKeyValue.ALL.forEach((tkv) -> this.storage.getValue(tkv).thenAccept((value) -> ymlStorage.setValue(tkv, value).thenAccept(success::add)));
    }
    return success.stream().allMatch((b) -> b);
  }
  /**
   * This method gets the value of a key either from the data map or from the storage
   * @param tkv The key to get the value of
   * @return The value of the key
   */
  public CompletableFuture<Object> getValue(TypedKeyValue<?> tkv) {
    return isUseInMemory() && data.containsKey(tkv)
      ? CompletableFuture.completedFuture(data.get(tkv))
      : storage.getValue(tkv);
  }
  /**
   * This method sets the value of a key, as well as updating the storage
   * @param tkv The key to set the value of
   * @param value The value to set
   */
  public CompletableFuture<Boolean> setValue(TypedKeyValue<?> tkv, Object value) {
    if(isUseInMemory()) data.put(tkv, value);
    return storage.setValue(tkv, value);
  }

  public void close() {
    storage.close();
  }


  public enum StorageType {
    YML,
    H2,
    MYSQL,
    SQLITE
  }
}
