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
package me.brandonli.murderrun.game.player.death;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.text;

import java.util.Collection;
import java.util.stream.Stream;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;

public final class KillerLocationTracker {

  private final Game game;

  public KillerLocationTracker(final Game game) {
    this.game = game;
  }

  public void spawnParticles() {
    final GamePlayerManager manager = this.game.getPlayerManager();
    final GameScheduler scheduler = this.game.getScheduler();
    final NullReference reference = NullReference.of();
    scheduler.scheduleRepeatedTask(() -> manager.applyToKillers(this::spawnParticlesWhenClose), 0, 20L, reference);
    scheduler.scheduleRepeatedTask(() -> manager.applyToKillers(this::spawnParticleTrail), 0, 1L, reference);
  }

  private void spawnParticleTrail(final GamePlayer killer) {
    final Location murdererLocation = killer.getLocation();
    final World killerWorld = requireNonNull(murdererLocation.getWorld());
    killerWorld.spawnParticle(Particle.DUST, murdererLocation, 3, 0.2, 0.2, 0.2, new DustOptions(Color.RED, 2));
  }

  private void spawnParticlesWhenClose(final GamePlayer murdererPlayer) {
    final GameScheduler scheduler = this.game.getScheduler();
    final Location murdererLocation = murdererPlayer.getLocation();
    final GamePlayerManager manager = this.game.getPlayerManager();
    final Stream<GamePlayer> survivors = manager.getLivingInnocentPlayers();
    final Collection<GamePlayer> players = survivors.toList();
    final World killerWorld = requireNonNull(murdererLocation.getWorld());
    final GameProperties properties = this.game.getProperties();
    final double radius = properties.getKillerParticleRadius();
    final double radiusSquared = radius * radius;
    final double halfRadius = radius / 2;
    final double halfRadiusSquared = halfRadius * halfRadius;
    for (final GamePlayer survivor : players) {
      final Survivor survivorPlayer = (Survivor) survivor;
      final Location location = survivorPlayer.getLocation();
      final World world = requireNonNull(location.getWorld());
      if (killerWorld != world) {
        continue;
      }

      final PlayerAudience audience = survivorPlayer.getAudience();
      final double distanceSquared = location.distanceSquared(murdererLocation);
      if (distanceSquared > radiusSquared) {
        survivorPlayer.setHeardSound(false);
        audience.setActionBar(text(""));
        continue;
      }

      if (!survivorPlayer.getHeardSound()) {
        audience.playSound("ambient.cave");
        survivorPlayer.setHeardSound(true);
      }

      final StrictPlayerReference reference = StrictPlayerReference.of(survivorPlayer);
      audience.playSound(Sounds.HEARTBEAT);
      if (distanceSquared < halfRadiusSquared) {
        final Location clone = location.clone().add(0, 1, 0);
        world.spawnParticle(Particle.DUST, clone, 15, 1, 1, 1, new DustOptions(Color.WHITE, 4));
        this.sendVeryCloseEffects(scheduler, audience, reference);
      } else {
        this.sendCloseEffects(audience, scheduler, reference);
      }
    }
  }

  private void sendCloseEffects(final PlayerAudience audience, final GameScheduler scheduler, final StrictPlayerReference reference) {
    audience.setActionBar(Message.HEARTBEAT_ACTION1.build());
    scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION2.build()), 10L, reference);
  }

  private void sendVeryCloseEffects(final GameScheduler scheduler, final PlayerAudience audience, final StrictPlayerReference reference) {
    scheduler.scheduleTask(() -> audience.playSound(Sounds.HEARTBEAT), 9L, reference);
    audience.setActionBar(Message.HEARTBEAT_ACTION1.build());
    scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION2.build()), 4L, reference);
    scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION3.build()), 8L, reference);
    scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION1.build()), 10L, reference);
    scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION2.build()), 14L, reference);
    scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION3.build()), 18L, reference);
  }

  public Game getGame() {
    return this.game;
  }
}
