package net.hobbnetwork.storage;

import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Represents a key-value pair that can be stored in the database, it also includes a type and a default value
 * <b>YOU SHOULD EXTEND THIS CLASS TO CREATE YOUR OWN KEY-VALUE PAIRS IN THE ALL LIST</b>
 * @param <T> The type of the value
 */
@Getter
public class TypedKeyValue<T> {
  public static List<TypedKeyValue<?>> ALL;
  private final Class<T> type;
  private final Supplier<T> defaultValue;
  private final String key;
  /**
   * This constructor creates a new TypedKeyValue object
   * @param key The name of the key
   * @param type The class of the value
   * @param defaultValue The default value of the key
   */
  public TypedKeyValue(String key, Class<T> type, Supplier<T> defaultValue){
    this.key = key;
    this.type = type;
    this.defaultValue = defaultValue;
  }

  public CompletableFuture<Boolean> initDefault(String key, H2Storage storage) {
    return this.exists(storage).thenCompose((has) -> {
      if(has) return CompletableFuture.completedFuture(false);
      return storage.setValue(this, defaultValue.get());
    });
  }
  /**
   * This method checks if this instance's key exists in the database
   * @param storage The storage to check
   * @return Whether the key exists
   */
  public CompletableFuture<Boolean> exists(H2Storage storage){
    return storage.getValue(this).thenApply(Objects::nonNull);
  }
}
