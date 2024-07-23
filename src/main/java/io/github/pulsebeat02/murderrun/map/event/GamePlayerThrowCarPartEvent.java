package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.game.GameWinCode;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.config.GameConfiguration;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.InnocentPlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.ItemStackUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.title.Title.title;

public final class GamePlayerThrowCarPartEvent implements Listener {

  private final MurderGame game;

  private final AtomicInteger carPartCount;

  public GamePlayerThrowCarPartEvent(final MurderGame game) {
    this.game = game;
    this.carPartCount = new AtomicInteger(0);
  }

  @EventHandler
  public void onPlayerThrowItem(final PlayerDropItemEvent event) {

    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    if (!ItemStackUtils.isCarPart(stack)) {
      return;
    }

    final GameConfiguration configuration = this.game.getConfiguration();
    final Location truckLocation = configuration.getTruckLocation();
    final Location itemLocation = item.getLocation();
    final double distSquared = itemLocation.distanceSquared(truckLocation);
    if (distSquared > 16) {
      return;
    }

    item.remove();

    final int count = this.carPartCount.incrementAndGet();
    final int goal = configuration.getCarPartCount();
    this.announceCarPartRetrieval(count, goal);
    this.checkGameEnd(count, goal);

    final Player thrower = event.getPlayer();
    if (this.checkIfPlayerStillHasCarPart(thrower)) {
      this.setPlayerCarPartStatus(thrower);
    }
  }

  private void checkGameEnd(final int count, final int goal) {
    if (count == goal) {
      this.game.finishGame(GameWinCode.INNOCENTS);
    }
  }

  private void setPlayerCarPartStatus(final Player thrower) {
    final PlayerManager manager = this.game.getPlayerManager();
    final UUID uuid = thrower.getUniqueId();
    final GamePlayer player = manager.lookupPlayer(uuid).orElseThrow();
    if (player instanceof final InnocentPlayer innocent) {
      innocent.setHasCarPart(false);
    }
  }

  private boolean checkIfPlayerStillHasCarPart(final Player thrower) {
    final PlayerInventory inventory = thrower.getInventory();
    final ItemStack[] contents = inventory.getContents();
    for (final ItemStack slot : contents) {
      if (ItemStackUtils.isCarPart(slot)) {
        return true;
      }
    }
    return false;
  }

  private void announceCarPartRetrieval(final int count, final int goal) {
    final int remaining = goal - count;
    final Component title = Locale.CAR_PART_RETRIEVAL.build(remaining);
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllParticipants(this.game, title, subtitle);
    AdventureUtils.playSoundForAllParticipants(this.game, Sound.BLOCK_ANVIL_USE);
  }
}
