package net.hobbnetwork.custom;

import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Setter
public class Triple<T,U,F> implements Storable<Triple<T,U,F>> {
  private T first;
  private U second;
  private F third;

  public Triple(@Nullable T first, @Nullable U second, @Nullable F third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  @Nullable public T fst() {
    return first;
  }

  @Nullable public U snd() {
    return second;
  }

  @Nullable public F thd() {
    return third;
  }

  @Override
  public String toString() {
    return "Triple{" +
      "first=" + first +
      ",second=" + second +
      ",third=" + third +
      '}';
  }
  @Override
  public Triple<T, U, F> fromString(String s) {
    String[] parts = s
      .replace("Triple{first=", "")
      .replace("second=", "")
      .replace("third=", "").split(",");
    try {
      return new Triple<>((T) parts[0], (U) parts[1], (F) parts[2]);
    } catch (Exception e) {
      return new Triple<>(null, null, null);
    }
  }
}
