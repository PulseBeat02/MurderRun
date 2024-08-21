package io.github.pulsebeat02.murderrun.game.map.event;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public final class GamePlayerPickupCarPartEvent implements Listener {

  private final Game game;

  public GamePlayerPickupCarPartEvent(final Game game) {
    this.game = game;
  }

  public Game getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerPickupItem(final EntityPickupItemEvent event) {

    final LivingEntity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    final PlayerManager playerManager = this.game.getPlayerManager();
    final boolean valid = playerManager.checkPlayerExists(player);
    if (!valid) {
      return;
    }

    final Item item = event.getItem();
    final ItemStack stack = item.getItemStack();
    final boolean isCarPart = ItemUtils.isCarPart(stack);
    final boolean isTrap = ItemUtils.isTrap(stack);
    if (!(isCarPart || isTrap)) {
      return;
    }

    if (isTrap) {
      event.setCancelled(true);
      return;
    }

    final GamePlayer gamePlayer = playerManager.getGamePlayer(player);
    if (!(gamePlayer instanceof Survivor survivor)) {
      event.setCancelled(true);
      return;
    }
    survivor.setHasCarPart(true);

    final Map map = this.game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final CarPart carPart = requireNonNull(manager.getCarPartItemStack(stack));
    carPart.setPickedUp(true);
  }
}
