package io.github.pulsebeat02.murderrun.game.player.death;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.awt.Color;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class KillerLocationTracker {

  private final Game game;

  public KillerLocationTracker(final Game game) {
    this.game = game;
  }

  public void spawnParticles() {
    final PlayerManager manager = this.game.getPlayerManager();
    final GameScheduler scheduler = this.game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllMurderers(this::spawnParticlesWhenClose), 0, 20);
  }

  private void spawnParticlesWhenClose(final GamePlayer murdererPlayer) {
    final Location murdererLocation = murdererPlayer.getLocation();
    final PlayerManager manager = this.game.getPlayerManager();
    final Collection<GamePlayer> survivors = manager.getAliveInnocentPlayers();
    for (final GamePlayer survivor : survivors) {
      final Location location = survivor.getLocation();
      if (location.distanceSquared(murdererLocation) > 16) {
        continue;
      }
      final Location clone = location.clone().add(0, 1, 0);
      final World world = requireNonNull(clone.getWorld());
      world.spawnParticle(Particle.DUST, clone, 10, 1, 1, 1, Color.WHITE);
    }
  }

  public Game getGame() {
    return this.game;
  }
}
