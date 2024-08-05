package io.github.pulsebeat02.murderrun.game.player.death;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Innocent;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import java.awt.*;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class MurdererLocationManager {

  private final MurderGame game;
  private final ScheduledExecutorService service;

  public MurdererLocationManager(final MurderGame game) {
    this.game = game;
    this.service = Executors.newScheduledThreadPool(1);
  }

  public void spawnParticles() {
    final MurderPlayerManager manager = this.game.getPlayerManager();

    this.service.scheduleAtFixedRate(
        () -> manager.applyToAllMurderers(this::spawnParticlesWhenClose), 0, 1, TimeUnit.SECONDS);
  }

  private void spawnParticlesWhenClose(final GamePlayer murdererPlayer) {
    final Location murdererLocation = murdererPlayer.getLocation();
    final MurderPlayerManager manager = this.game.getPlayerManager();
    final Collection<Innocent> innocents = manager.getInnocentPlayers();
    for (final Innocent innocent : innocents) {
      final Location location = innocent.getLocation();
      if (location.distanceSquared(murdererLocation) > 16) {
        continue;
      }
      final Location clone = location.clone().add(0, 1, 0);
      final World world = clone.getWorld();
      if (world == null) {
        throw new AssertionError("Location doesn't have World attached to it!");
      }
      world.spawnParticle(Particle.DUST, clone, 10, 1, 1, 1, Color.WHITE);
    }
  }

  public MurderGame getGame() {
    return this.game;
  }

  public ScheduledExecutorService getService() {
    return this.service;
  }

  public void shutdownExecutor() {
    this.service.shutdown();
  }
}
