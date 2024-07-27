package io.github.pulsebeat02.murderrun.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.math.BlockVector3;
import io.github.pulsebeat02.murderrun.json.adapters.BlockVector3Adapter;
import io.github.pulsebeat02.murderrun.json.adapters.ItemStackAdapter;
import io.github.pulsebeat02.murderrun.json.adapters.LocationAdapter;
import io.github.pulsebeat02.murderrun.json.adapters.PathAdapter;
import java.nio.file.Path;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public final class GsonProvider {

  private static final Gson GSON;

  static {
    final GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(Location.class, new LocationAdapter());
    builder.registerTypeAdapter(ItemStack.class, new ItemStackAdapter());
    builder.registerTypeHierarchyAdapter(BlockVector3.class, new BlockVector3Adapter());
    builder.registerTypeHierarchyAdapter(Path.class, new PathAdapter());
    GSON = builder.create();
  }

  public static Gson getGson() {
    return GSON;
  }
}
