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
package me.brandonli.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.EntityReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.HorseInventory;

public final class DeathSteed extends KillerGadget {

  public DeathSteed() {
    super(
      "death_steed",
      GameProperties.DEATH_STEED_COST,
      ItemFactory.createGadget(
        "death_steed",
        GameProperties.DEATH_STEED_MATERIAL,
        Message.DEATH_STEED_NAME.build(),
        Message.DEATH_STEED_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final Horse horse = this.spawnHorse(world, location, player);
    final GameScheduler scheduler = game.getScheduler();
    final GamePlayerManager manager = game.getPlayerManager();
    final EntityReference reference = EntityReference.of(horse);
    scheduler.scheduleRepeatedTask(() -> this.handleSurvivors(manager, horse), 0, 5L, reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.DEATH_STEED_SOUND);

    return false;
  }

  private void handleSurvivors(final GamePlayerManager manager, final Horse horse) {
    manager.applyToLivingSurvivors(survivor -> this.handleSurvivor(survivor, horse));
  }

  private Horse spawnHorse(final World world, final Location location, final GamePlayer player) {
    return world.spawn(location, Horse.class, entity -> {
      player.apply(owner -> {
        this.customizeAttributes(entity, owner);
        this.setSaddle(entity);
      });
    });
  }

  private void customizeAttributes(final Horse entity, final Player owner) {
    entity.setTamed(true);
    entity.setOwner(owner);
    entity.addPassenger(owner);
    entity.setColor(Color.BLACK);
  }

  private void setSaddle(final Horse horse) {
    final HorseInventory inventory = horse.getInventory();
    inventory.setSaddle(ItemFactory.createSaddle());
  }

  private void handleSurvivor(final GamePlayer survivor, final Horse horse) {
    final Location survivorLocation = survivor.getLocation();
    final Location updated = survivorLocation.add(0, 2, 0);
    final Location horseLocation = horse.getLocation();
    final double distance = updated.distanceSquared(horseLocation);
    if (distance < 400) {
      this.spawnParticleLine(updated, horseLocation);
    }
  }

  private void spawnParticleLine(final Location start, final Location end) {
    final World world = requireNonNull(start.getWorld());
    final double distance = start.distance(end) - 3;
    final double step = 0.1;
    for (double d = 0; d < distance; d += step) {
      final double t = d / distance;
      final double x = start.getX() + (end.getX() - start.getX()) * t;
      final double y = start.getY() + (end.getY() - start.getY()) * t;
      final double z = start.getZ() + (end.getZ() - start.getZ()) * t;
      world.spawnParticle(Particle.BUBBLE, x, y, z, 5);
    }
  }
}
