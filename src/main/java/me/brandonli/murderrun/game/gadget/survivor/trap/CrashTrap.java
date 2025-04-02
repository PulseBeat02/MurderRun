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
package me.brandonli.murderrun.game.gadget.survivor.trap;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

import java.awt.Color;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class CrashTrap extends SurvivorTrap {

  public CrashTrap() {
    super(
      "crash_trap",
      Integer.MAX_VALUE,
      ItemFactory.createGadget(
        "crash_trap",
        Material.STRUCTURE_VOID,
        text("Crash Trap", RED),
        text("Crashes the client (wtf, use at your own risk)", RED)
      ),
      empty(),
      Color.RED
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer activee, final Item item) {
    activee.kick("How did you get crashed...");
  }
}
