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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public final class SpasmTrap extends SurvivorTrap {

  private static final Vector UP = new Vector(0, 1, 0);
  private static final Vector DOWN = new Vector(0, -1, 0);

  private final Map<GamePlayer, AtomicBoolean> states;

  public SpasmTrap() {
    super(
      "spasm_trap",
      GameProperties.SPASM_COST,
      ItemFactory.createGadget("spasm_trap", GameProperties.SPASM_MATERIAL, Message.SPASM_NAME.build(), Message.SPASM_LORE.build()),
      Message.SPASM_ACTIVATE.build(),
      GameProperties.SPASM_COLOR
    );
    this.states = new HashMap<>();
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(murderer);
    scheduler.scheduleRepeatedTask(() -> this.alternateHead(murderer), 0, 5, GameProperties.SPASM_DURATION, reference);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.SPASM_SOUND);
  }

  private void alternateHead(final GamePlayer murderer) {
    final Function<GamePlayer, AtomicBoolean> function = ignored -> new AtomicBoolean(false);
    final AtomicBoolean atomic = this.states.computeIfAbsent(murderer, function);
    final boolean up = atomic.get();
    final Location location = this.getProperLocation(murderer, up);
    murderer.teleport(location);
    atomic.set(!up);
  }

  private Location getProperLocation(final GamePlayer murderer, final boolean up) {
    final Location location = murderer.getLocation();
    location.setDirection(up ? UP : DOWN);
    return location;
  }
}
