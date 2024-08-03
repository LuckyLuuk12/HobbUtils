package net.hobb.storage;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class H2Storage {

  // TODO: Implement this class
  public CompletableFuture<Boolean> setValue(Object key, TypedKeyValue<?> keyVal, Object value) {
    return CompletableFuture.completedFuture(true);
  }
  public CompletableFuture<Object> getValue(Object key, TypedKeyValue<?> keyVal) {
    return CompletableFuture.completedFuture(null);
  }

  /**
   * Represents a key-value pair that can be stored in the database, it also includes a type and a default value
   * @param <T> The type of the value
   */
  static public abstract class TypedKeyValue<T> {
    @Getter @Setter
    private static List<TypedKeyValue<?>> STATS;
    @Getter
    private final Class<T> clazz;
    @Getter
    private final Supplier<T> defaultValue;
    @Getter
    private final String name;
    /**
     * This constructor creates a new TypedKeyValue object
     * @param name The name of the key
     * @param clazz The class of the value
     * @param defaultValue The default value of the key
     */
    TypedKeyValue(String name, Class<T> clazz, Supplier<T> defaultValue){
      this.name = name;
      this.clazz = clazz;
      this.defaultValue = defaultValue;
    }

    public CompletableFuture<Boolean> initDefault(UUID player, H2Storage storage) {
      return this.exists(player, storage).thenCompose((has) -> {
        if(has) return CompletableFuture.completedFuture(false);
        return storage.setValue(player,this, defaultValue.get());
      });
    }
    /**
     * This method checks if the key exists in the storage
     * @param key The key to check
     * @param storage The storage to check
     * @return Whether the key exists
     */
    public CompletableFuture<Boolean> exists(Object key, H2Storage storage){
      return storage.getValue(key, this).thenApply(Objects::nonNull);
    }
  }
}
