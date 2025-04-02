/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.math.BlockVector3;
import io.github.pulsebeat02.murderrun.gson.adapters.BlockVectorAdapter;
import io.github.pulsebeat02.murderrun.gson.adapters.ItemStackAdapter;
import io.github.pulsebeat02.murderrun.gson.adapters.LocationAdapter;
import io.github.pulsebeat02.murderrun.gson.adapters.PathAdapter;
import java.nio.file.Path;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public final class GsonProvider {

  private static final Gson GSON;

  static {
    final GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(Location.class, new LocationAdapter());
    builder.registerTypeAdapter(ItemStack.class, new ItemStackAdapter());
    builder.registerTypeHierarchyAdapter(BlockVector3.class, new BlockVectorAdapter());
    builder.registerTypeHierarchyAdapter(Path.class, new PathAdapter());
    builder.serializeNulls();
    GSON = builder.create();
  }

  private GsonProvider() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Gson getGson() {
    return GSON;
  }
}
