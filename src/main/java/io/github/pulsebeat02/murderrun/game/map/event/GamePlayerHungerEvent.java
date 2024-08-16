package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public final class GamePlayerHungerEvent implements Listener {

  private final Game game;

  public GamePlayerHungerEvent(final Game game) {
    this.game = game;
  }

  public Game getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onHungerDeplete(final FoodLevelChangeEvent event) {

    final HumanEntity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    final PlayerManager manager = this.game.getPlayerManager();
    final boolean valid = manager.checkPlayerExists(player);
    if (!valid) {
      return;
    }

    event.setCancelled(true);
    event.setFoodLevel(20);
  }
}
