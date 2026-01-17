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
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Participant;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public final class ShockwaveTrap extends SurvivorTrap {

  public ShockwaveTrap(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "shockwave_trap",
        properties.getShockwaveCost(),
        ItemFactory.createGadget(
            "shockwave_trap",
            properties.getShockwaveMaterial(),
            Message.SHOCKWAVE_NAME.build(),
            Message.SHOCKWAVE_LORE.build()),
        Message.SHOCKWAVE_ACTIVATE.build(),
        properties.getShockwaveColor());
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer survivor, final Item item) {
    final Location origin = item.getLocation();
    final World world = requireNonNull(origin.getWorld());
    world.createExplosion(origin, 0, false, false);

    final GameProperties properties = game.getProperties();
    final GamePlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(participant -> this.applyShockwave(participant, origin));
    manager.playSoundForAllParticipants(properties.getShockwaveSound());
  }

  private void applyShockwave(final Participant participant, final Location origin) {
    final Location location = participant.getLocation();
    final double distance = location.distanceSquared(origin);
    final Game game = participant.getGame();
    final GameProperties properties = game.getProperties();
    final double radius = properties.getShockwaveExplosionRadius();
    if (distance < radius * radius) {
      final Vector playerVector = location.toVector();
      final Vector blockVector = origin.toVector();
      final Vector vector = playerVector.subtract(blockVector);
      vector.normalize();
      vector.multiply(properties.getShockwaveExplosionPower());
      participant.setVelocity(vector);
    }
  }
}
