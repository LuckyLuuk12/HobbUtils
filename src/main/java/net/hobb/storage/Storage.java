package net.hobb.storage;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class Storage {
  abstract public CompletableFuture<Boolean> init(@NotNull String configName, String... path);
  abstract public CompletableFuture<Boolean> setValue(@NotNull TypedKeyValue<?> key, @NotNull Object value);
  abstract public CompletableFuture<Object> getValue(@NotNull TypedKeyValue<?> key);
  abstract public CompletableFuture<Boolean> removeValue(@NotNull TypedKeyValue<?> key);
}
