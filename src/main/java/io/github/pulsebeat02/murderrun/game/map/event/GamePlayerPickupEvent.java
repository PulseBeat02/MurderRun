package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public final class GamePlayerPickupEvent extends GameEvent {

  public GamePlayerPickupEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerPickupItem(final EntityPickupItemEvent event) {
    final LivingEntity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    if (!this.isGamePlayer(player)) {
      return;
    }

    final Game game = this.getGame();
    final PlayerManager playerManager = game.getPlayerManager();
    final Item item = event.getItem();
    final ItemStack stack = item.getItemStack();
    final boolean isCarPart = PDCUtils.isCarPart(stack);
    final boolean isTrap = PDCUtils.isTrap(stack);
    if (!(isCarPart || isTrap)) {
      return;
    }

    if (isTrap) {
      event.setCancelled(true);
      return;
    }

    final GamePlayer gamePlayer = playerManager.getGamePlayer(player);
    if (gamePlayer instanceof Killer) {
      event.setCancelled(true);
      return;
    }

    final Survivor survivor = (Survivor) gamePlayer;
    if (!survivor.canPickupCarPart()) {
      event.setCancelled(true);
      return;
    }
    survivor.setHasCarPart(true);

    final Map map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final CarPart carPart = manager.getCarPartItemStack(stack);
    if (carPart != null) {
      carPart.setPickedUp(true);
    }
  }
}
