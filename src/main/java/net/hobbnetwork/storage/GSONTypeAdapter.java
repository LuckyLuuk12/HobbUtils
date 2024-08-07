package net.hobbnetwork.storage;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class GSONTypeAdapter<T> extends TypeAdapter<T> {
  private final Class<T> type;

  public GSONTypeAdapter(Class<T> type) {
    this.type = type;
  }

  @Override
  public void write(JsonWriter out, T value) throws IOException {
    Map<String, Object> jsonMap = new HashMap<>();
    for (Method method : type.getDeclaredMethods()) {
      if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
        try {
          String fieldName = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
          jsonMap.put(fieldName, method.invoke(value));
        } catch (Exception e) {
          throw new IOException("[GSONTypeAdapter] Failed to invoke getter: " + method.getName() + "\n" + e);
        }
      }
    }
    out.jsonValue(new Gson().toJson(jsonMap));
  }

  @Override
  public T read(JsonReader in) throws IOException {
    try {
      return new Gson().fromJson(in, type);
    } catch (Exception e) {
      throw new IOException("[GSONTypeAdapter] Failed to read JSON into " + type.getName() + "\n" + e);
    }
  }
}
