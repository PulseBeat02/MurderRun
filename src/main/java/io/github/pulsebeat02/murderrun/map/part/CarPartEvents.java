package io.github.pulsebeat02.murderrun.map.part;

import io.github.pulsebeat02.murderrun.MurderGame;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.InnocentPlayer;
import io.github.pulsebeat02.murderrun.player.Murderer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;
import java.util.UUID;

public final class CarPartEvent implements Listener {

  private final MurderGame game;

  public CarPartEvent(final MurderGame game) {
    this.game = game;
  }

  @EventHandler
  public void onPlayerPickupItem(final PlayerAttemptPickupItemEvent event) {

    final Item item = event.getItem();
    final ItemStack stack = item.getItemStack();
    final ItemMeta meta = stack.getItemMeta();
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    final NamespacedKey key = MurderRun.getKey();
    final String data = container.get(key, PersistentDataType.STRING);
    final String check = CarPart.getPdcId();
    if (data == null || !data.equals(check)) {
      return;
    }

    final UUID owner = item.getOwner();
    final PlayerManager manager = this.game.getPlayerManager();
    final Optional<GamePlayer> player = manager.lookupPlayer(owner);
    if (player.isEmpty()) {
      return;
    }

    final GamePlayer gamePlayer = player.get();
    if (gamePlayer instanceof final InnocentPlayer innocent) {
      innocent.setHasCarPart(true);
      item.remove();
    } else if (gamePlayer instanceof Murderer) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerThrowItem(final Player event) {

  }

}
