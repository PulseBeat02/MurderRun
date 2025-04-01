package io.github.pulsebeat02.murderrun.game.lobby;

import io.github.pulsebeat02.murderrun.game.GameProperties;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public final class PreGameExpirationTimer extends BukkitRunnable {

  private final PreGamePlayerManager manager;
  private final AtomicInteger seconds;

  public PreGameExpirationTimer(final PreGamePlayerManager manager) {
    this.manager = manager;
    this.seconds = new AtomicInteger(0);
  }

  @Override
  public void run() {
    final int current = this.seconds.incrementAndGet();
    final int shutdownSeconds = GameProperties.GAME_EXPIRATION_TIME;
    if (current >= shutdownSeconds) {
      final PreGameManager preGameManager = this.manager.getManager();
      final GameManager gameManager = preGameManager.getGameManager();
      final String id = preGameManager.getId();
      gameManager.removeGame(id);
      this.cancel();
    }
  }
}
