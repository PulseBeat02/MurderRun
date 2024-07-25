package io.github.pulsebeat02.murderrun.player.death;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.InnocentPlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import java.awt.*;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class MurdererLocationManager {

  private final MurderGame game;
  private final ScheduledExecutorService service;

  public MurdererLocationManager(final MurderGame game) {
    this.game = game;
    this.service = Executors.newScheduledThreadPool(1);
  }

  public void spawnParticles() {
    final PlayerManager manager = this.game.getPlayerManager();
    this.service.scheduleAtFixedRate(
        () -> manager.getMurderers().forEach(this::spawnParticlesWhenClose),
        0,
        1,
        TimeUnit.SECONDS);
  }

  private void spawnParticlesWhenClose(final GamePlayer murdererPlayer) {
    final Player murderer = murdererPlayer.getPlayer();
    final Location murdererLocation = murderer.getLocation();
    final PlayerManager manager = this.game.getPlayerManager();
    final Collection<InnocentPlayer> innocentPlayers = manager.getInnocentPlayers();
    for (final InnocentPlayer innocentPlayer : innocentPlayers) {
      final Player player = innocentPlayer.getPlayer();
      final Location location = player.getLocation();
      if (location.distanceSquared(murdererLocation) > 16) {
        continue;
      }
      final Location clone = location.clone().add(0, 1, 0);
      final World world = clone.getWorld();
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
