/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.math.BlockVector3;
import java.nio.file.Path;
import me.brandonli.murderrun.utils.gson.adapters.BlockVectorAdapter;
import me.brandonli.murderrun.utils.gson.adapters.ItemStackAdapter;
import me.brandonli.murderrun.utils.gson.adapters.LocationAdapter;
import me.brandonli.murderrun.utils.gson.adapters.PathAdapter;
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
