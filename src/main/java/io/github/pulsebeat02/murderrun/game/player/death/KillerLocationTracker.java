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
package io.github.pulsebeat02.murderrun.game.player.death;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.text;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.StrictPlayerReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import java.util.Collection;
import java.util.stream.Stream;
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
    final double radius = GameProperties.KILLER_PARTICLE_RADIUS;
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

      audience.playSound(Sounds.HEARTBEAT);

      final StrictPlayerReference reference = StrictPlayerReference.of(survivorPlayer);
      if (distanceSquared < halfRadiusSquared) {
        scheduler.scheduleTask(() -> audience.playSound(Sounds.HEARTBEAT), 9L, reference);
        audience.setActionBar(Message.HEARTBEAT_ACTION1.build());
        scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION2.build()), 4L, reference);
        scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION3.build()), 8L, reference);
        scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION1.build()), 10L, reference);
        scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION2.build()), 14L, reference);
        scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION3.build()), 18L, reference);
      } else {
        audience.setActionBar(Message.HEARTBEAT_ACTION1.build());
        scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION2.build()), 7L, reference);
        scheduler.scheduleTask(() -> audience.setActionBar(Message.HEARTBEAT_ACTION3.build()), 14L, reference);
      }

      final Location clone = location.clone().add(0, 1, 0);
      world.spawnParticle(Particle.DUST, clone, 15, 1, 1, 1, new DustOptions(Color.WHITE, 4));
    }
  }

  public Game getGame() {
    return this.game;
  }
}
