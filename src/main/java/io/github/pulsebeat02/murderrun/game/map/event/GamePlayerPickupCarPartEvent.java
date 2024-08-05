package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.map.MurderMap;
import io.github.pulsebeat02.murderrun.game.map.part.CarPartItemStack;
import io.github.pulsebeat02.murderrun.game.map.part.CarPartManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.utils.ItemStackUtils;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public final class GamePlayerPickupCarPartEvent implements Listener {

  private final MurderGame game;

  public GamePlayerPickupCarPartEvent(final MurderGame game) {
    this.game = game;
  }

  public MurderGame getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerPickupItem(final EntityPickupItemEvent event) {

    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    final Item item = event.getItem();
    final ItemStack stack = item.getItemStack();
    if (!ItemStackUtils.isCarPart(stack)) {
      return;
    }

    final UUID owner = item.getOwner();
    if (owner == null) {
      return;
    }

    final Player player = Bukkit.getPlayer(owner);
    if (player == null) {
      return;
    }

    final Optional<GamePlayer> optional = PlayerUtils.checkIfValidEventPlayer(this.game, player);
    if (optional.isEmpty()) {
      return;
    }

    final GamePlayer gamePlayer = optional.get();
    gamePlayer.onPlayerAttemptPickupPartEvent(event);

    final MurderMap map = this.game.getMurderMap();
    final CarPartManager manager = map.getCarPartManager();
    final CarPartItemStack carPartItemStack = manager.getCarPartItemStack(stack);
    if (carPartItemStack == null) {
      throw new AssertionError("Failed to retrieve car part from game!");
    }
    carPartItemStack.setPickedUp(true);
  }
}
