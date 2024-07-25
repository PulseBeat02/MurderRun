package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public final class GamePlayerHungerEvent implements Listener {

  private final MurderGame game;

  public GamePlayerHungerEvent(final MurderGame game) {
    this.game = game;
  }

  public MurderGame getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onHungerDeplete(final FoodLevelChangeEvent event) {

    final HumanEntity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    final UUID uuid = player.getUniqueId();
    final PlayerManager manager = this.game.getPlayerManager();
    final Optional<GamePlayer> optional = manager.lookupPlayer(uuid);
    if (optional.isEmpty()) {
      return;
    }

    event.setCancelled(true);
    event.setFoodLevel(20);
  }
}
