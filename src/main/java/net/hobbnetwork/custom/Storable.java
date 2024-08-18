package net.hobbnetwork.custom;

import org.jetbrains.annotations.Nullable;
import net.hobbnetwork.storage.H2Storage;
import net.hobbnetwork.storage.TypedKeyValue;

/**
 * Interface for objects that can be stored as strings.<br>
 * <b>NOTE:</b> The classes that implement this interface should have a no-args constructor!<br>
 * This allows the {@link H2Storage} class to create new instances of the object
 * in the {@link H2Storage#getValue(TypedKeyValue)} method.
 * @param <T> The type of object to store
 */
public interface Storable<T> {
  @Nullable String toString();
  @Nullable T fromString(@Nullable String s);
}
