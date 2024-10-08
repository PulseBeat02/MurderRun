package io.github.pulsebeat02.murderrun.game.event;

import io.github.pulsebeat02.murderrun.game.lobby.PreGameManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGamePlayerManager;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.Collection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class DupePreventListener implements Listener {

  private final PreGameManager manager;

  public DupePreventListener(final PreGameManager manager) {
    this.manager = manager;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onItemDrop(final PlayerDropItemEvent event) {
    final Player player = event.getPlayer();
    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final Collection<Player> participants = playerManager.getParticipants();
    if (!participants.contains(player)) {
      return;
    }

    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    final ItemStack other = ItemFactory.createCurrency(1);
    if (!stack.isSimilar(other)) {
      return;
    }

    event.setCancelled(true);
  }
}
