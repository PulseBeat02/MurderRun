package io.github.pulsebeat02.murderrun.game.map.event;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameResult;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class GamePlayerThrowCarPartEvent implements Listener {

  private final Game game;

  public GamePlayerThrowCarPartEvent(final Game game) {
    this.game = game;
  }

  public Game getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerThrowItem(final PlayerDropItemEvent event) {

    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    if (!ItemUtils.isCarPart(stack)) {
      return;
    }

    final GameSettings configuration = this.game.getSettings();
    final Arena arena = requireNonNull(configuration.getArena());
    final Location truckLocation = arena.getTruck();
    final Location itemLocation = item.getLocation();
    final double distSquared = itemLocation.distanceSquared(truckLocation);
    if (distSquared > 16) {
      event.setCancelled(true);
      return;
    }

    item.remove();

    final Map map = this.game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final CarPart carPart = requireNonNull(manager.getCarPartItemStack(stack));

    manager.removeCarPart(carPart);

    final java.util.Map<String, CarPart> carPartItemStackMap = manager.getParts();
    final int leftOver = carPartItemStackMap.size();
    this.announceCarPartRetrieval(leftOver);
    this.setBossBar(leftOver);
    this.checkGameEnd(leftOver);

    final Player thrower = event.getPlayer();
    if (!this.checkIfPlayerStillHasCarPart(thrower)) {
      this.setPlayerCarPartStatus(thrower);
    }
  }

  private void announceCarPartRetrieval(final int leftOver) {
    final Component title = Message.CAR_PART_ITEM_RETRIEVAL.build(leftOver);
    final PlayerManager manager = this.game.getPlayerManager();
    manager.sendMessageToAllParticipants(title);
    manager.playSoundForAllParticipants("block.anvil.use");
  }

  private void setBossBar(final int leftOver) {
    final GameSettings settings = this.game.getSettings();
    final int parts = settings.getCarPartCount();
    final int collected = parts - leftOver;
    final Component name = Message.BOSS_BAR.build(collected, parts);
    final float progress = 0f;
    final BossBar.Color color = BossBar.Color.GREEN;
    final BossBar.Overlay overlay = BossBar.Overlay.NOTCHED_20;
    final PlayerManager manager = this.game.getPlayerManager();
    manager.showBossBarForAllParticipants(name, progress, color, overlay);
  }

  private void checkGameEnd(final int leftOver) {
    if (leftOver == 0) {
      this.game.finishGame(GameResult.INNOCENTS);
    }
  }

  private boolean checkIfPlayerStillHasCarPart(final Player thrower) {
    final PlayerInventory inventory = thrower.getInventory();
    final ItemStack[] contents = inventory.getContents();
    for (final ItemStack slot : contents) {
      if (ItemUtils.isCarPart(slot)) {
        return true;
      }
    }
    return false;
  }

  private void setPlayerCarPartStatus(final Player thrower) {

    final PlayerManager manager = this.game.getPlayerManager();
    final boolean exists = manager.checkPlayerExists(thrower);
    if (!exists) {
      return;
    }

    final GamePlayer player = manager.getGamePlayer(thrower);
    if (player instanceof final Survivor survivor) {
      survivor.setHasCarPart(false);
    }
  }
}
