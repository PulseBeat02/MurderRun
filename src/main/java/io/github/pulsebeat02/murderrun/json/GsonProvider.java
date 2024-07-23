package io.github.pulsebeat02.murderrun.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.pulsebeat02.murderrun.json.adapters.LocationAdapter;
import org.bukkit.Location;

public final class GsonProvider {

  private static final Gson GSON;

  static {
    final GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(Location.class, new LocationAdapter());
    GSON = builder.create();
  }

  public static Gson getGson() {
    return GSON;
  }
}
