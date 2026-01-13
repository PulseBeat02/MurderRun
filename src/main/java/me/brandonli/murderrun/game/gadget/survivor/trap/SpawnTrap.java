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

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class SpawnTrap extends SurvivorTrap {

  public SpawnTrap(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "spawn_trap",
      properties.getSpawnCost(),
      ItemFactory.createGadget("spawn_trap", properties.getSpawnMaterial(), Message.SPAWN_NAME.build(), Message.SPAWN_LORE.build()),
      Message.SPAWN_ACTIVATE.build(),
      properties.getSpawnColor()
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location spawn = arena.getSpawn();
    murderer.teleport(spawn);

    final GameProperties properties = game.getProperties();
    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(properties.getSpawnSound());
  }
}
