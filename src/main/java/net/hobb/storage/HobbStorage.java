package net.hobb.storage;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class HobbStorage {

  abstract public CompletableFuture<Boolean> init(@NotNull String configName, String... path);
  abstract public CompletableFuture<Boolean> setValue(@NotNull TypedKeyValue<?> key, @NotNull Object value);
  abstract public CompletableFuture<Object> getValue(@NotNull TypedKeyValue<?> key);
}
