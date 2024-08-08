package net.hobbnetwork.storage;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Represents a key-value pair that can be stored in the database, it also includes a type and a default value
 * <b>ENSURE THAT THE VALUE TYPE IMPLEMENTS {@link Serializable}</b>
 * @param <T> The type of the value
 */
@Getter
public class TypedKeyValue<T extends Serializable> {
  public static List<TypedKeyValue<?>> ALL;
  private final Class<T> type;
  private final Supplier<T> defaultValue;
  private final String key;
  /**
   * This constructor creates a new TypedKeyValue object
   * @param key The name of the key
   * @param type The class of the value which should implement Serializable
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

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  /**
   * This method checks whether a TypedKeyValue object is equal to another object
   * NOTE that this only checks if the key is equal
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TypedKeyValue<?> that = (TypedKeyValue<?>) o;
    return key.equals(that.key);
  }
}
