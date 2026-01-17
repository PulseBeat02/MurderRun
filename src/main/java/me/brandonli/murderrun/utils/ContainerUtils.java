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
package me.brandonli.murderrun.utils;

import dev.triumphteam.gui.components.GuiContainer;
import dev.triumphteam.gui.components.InventoryProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public final class ContainerUtils {

  private ContainerUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  private static final InventoryProvider.Chest INVENTORY_PROVIDER =
      (title, owner, rows) -> Bukkit.createInventory(owner, rows, title);

  public static GuiContainer.Chest createChestContainer(final Component title, final int rows) {
    return new GuiContainer.Chest(title, INVENTORY_PROVIDER, rows);
  }
}
