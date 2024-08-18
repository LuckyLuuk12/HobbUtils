package net.hobbnetwork.custom;

import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Setter
public class Tuple<T,U> implements Storable<Tuple<T,U>> {
  private T first;
  private U second;

  public Tuple(@Nullable T first, @Nullable U second) {
    this.first = first;
    this.second = second;
  }

  @Nullable public T fst() {
    return first;
  }

  @Nullable public U snd() {
    return second;
  }

  @Override
  public String toString() {
    return "Tuple{" +
      "first=" + first +
      ",second=" + second +
      '}';
  }
  @Override
  public Tuple<T, U> fromString(String s) {
    String[] parts = s
      .replace("Tuple{first=", "")
      .replace("second=", "").split(",");
    try {
      return new Tuple<>((T) parts[0], (U) parts[1]);
    } catch (Exception e) {
      return new Tuple<>(null, null);
    }
  }
}
