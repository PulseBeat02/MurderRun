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
import me.brandonli.murderrun.game.player.death.DeathManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Item;

public final class BurnTheBody extends KillerGadget {

  public BurnTheBody(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "burn_the_body",
        properties.getBurnTheBodyCost(),
        ItemFactory.createGadget(
            "burn_the_body",
            properties.getBurnTheBodyMaterial(),
            Message.BURN_THE_BODY_NAME.build(),
            Message.BURN_THE_BODY_LORE.build()));
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    final Location location = player.getLocation();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer closest = manager.getNearestDeadSurvivor(location);
    if (closest == null) {
      return true;
    }

    final Location deathLocation = requireNonNull(closest.getDeathLocation());
    final double distance = location.distanceSquared(deathLocation);
    final GameProperties properties = game.getProperties();
    final double radius = properties.getBurnTheBodyRadius();
    if (distance > radius * radius) {
      return true;
    }
    item.remove();

    final GameScheduler scheduler = game.getScheduler();
    this.destroyBody(scheduler, closest, deathLocation);
    manager.playSoundForAllParticipants(properties.getBurnTheBodySound());

    return false;
  }

  private void destroyBody(
      final GameScheduler scheduler, final GamePlayer victim, final Location deathLocation) {
    final World world = requireNonNull(deathLocation.getWorld());
    final StrictPlayerReference reference = StrictPlayerReference.of(victim);
    scheduler.scheduleRepeatedTask(
        () -> this.summonEffects(deathLocation, world), 0, 20L, 5 * 20L, reference);
    scheduler.scheduleTask(() -> this.handleBurnTasks(victim), 5 * 20L, reference);
  }

  private void summonEffects(final Location deathLocation, final World world) {
    world.spawnParticle(Particle.LAVA, deathLocation, 15, 1, 1, 1);
    world.strikeLightningEffect(deathLocation);
  }

  private void handleBurnTasks(final GamePlayer victim) {
    final DeathManager manager = victim.getDeathManager();
    final NPC stand = manager.getCorpse();
    if (stand != null) {
      stand.despawn();
    }

    victim.setLastDeathLocation(null);
  }
}
