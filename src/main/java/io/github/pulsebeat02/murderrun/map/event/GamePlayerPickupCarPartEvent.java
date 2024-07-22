package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.utils.ItemStackUtils;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public final class GamePlayerPickupCarPartEvent implements Listener {

  private final MurderGame game;

  public GamePlayerPickupCarPartEvent(final MurderGame game) {
    this.game = game;
  }

  @EventHandler
  public void onPlayerPickupItem(final PlayerAttemptPickupItemEvent event) {

    final Item item = event.getItem();
    final ItemStack stack = item.getItemStack();
    if (!ItemStackUtils.isCarPart(stack)) {
      return;
    }

    final UUID owner = item.getOwner();
    final PlayerManager manager = this.game.getPlayerManager();
    final Optional<GamePlayer> player = manager.lookupPlayer(owner);
    if (player.isEmpty()) {
      return;
    }

    final GamePlayer gamePlayer = player.get();
    gamePlayer.onPlayerAttemptPickupPartEvent(event);
  }
}
