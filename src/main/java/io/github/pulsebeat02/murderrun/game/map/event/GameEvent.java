package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class GameEvent implements Listener {

  private final Game game;

  public GameEvent(final Game game) {
    this.game = game;
  }

  public boolean isGamePlayer(final Player player) {
    final PlayerManager manager = this.game.getPlayerManager();
    return manager.checkPlayerExists(player);
  }

  public Game getGame() {
    return this.game;
  }
}
