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
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerScoreboard;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class GamePlayerThrowCarPartEvent extends GameEvent {

  public GamePlayerThrowCarPartEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerThrowItem(final PlayerDropItemEvent event) {
    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    if (!PDCUtils.isCarPart(stack)) {
      return;
    }

    final Game game = this.getGame();
    final GameSettings configuration = game.getSettings();
    final Arena arena = requireNonNull(configuration.getArena());
    final Location truckLocation = arena.getTruck();
    final Location itemLocation = item.getLocation();
    final double distSquared = itemLocation.distanceSquared(truckLocation);
    if (distSquared > 25) {
      event.setCancelled(true);
      return;
    }

    item.remove();

    final Map map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final CarPart carPart = manager.getCarPartItemStack(stack);
    if (carPart == null) {
      return;
    }

    manager.removeCarPart(carPart);

    final int leftOver = manager.getRemainingParts();
    this.setScoreboard();
    this.announceCarPartRetrieval(leftOver);
    if (this.checkGameEnd(leftOver)) {
      return;
    }

    final Player thrower = event.getPlayer();
    if (!this.checkIfPlayerStillHasCarPart(thrower)) {
      this.setPlayerCarPartStatus(thrower);
    }
  }

  private void setScoreboard() {
    final Game game = this.getGame();
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllParticipants(player -> {
      final MetadataManager metadata = player.getMetadataManager();
      final PlayerScoreboard scoreboard = metadata.getSidebar();
      scoreboard.updateSidebar();
    });
  }

  private void announceCarPartRetrieval(final int leftOver) {
    final Game game = this.getGame();
    final Component title = Message.CAR_PART_ITEM_RETRIEVAL.build(leftOver);
    final PlayerManager manager = game.getPlayerManager();
    manager.sendMessageToAllParticipants(title);
    manager.playSoundForAllParticipants("block.anvil.use");
  }

  private boolean checkGameEnd(final int leftOver) {
    if (leftOver == 0) {
      final Game game = this.getGame();
      game.finishGame(GameResult.INNOCENTS);
      return true;
    }
    return false;
  }

  private boolean checkIfPlayerStillHasCarPart(final Player thrower) {
    final PlayerInventory inventory = thrower.getInventory();
    final ItemStack[] contents = inventory.getContents();
    for (final ItemStack slot : contents) {
      if (PDCUtils.isCarPart(slot)) {
        return true;
      }
    }
    return false;
  }

  private void setPlayerCarPartStatus(final Player thrower) {
    if (!this.isGamePlayer(thrower)) {
      return;
    }

    final Game game = this.getGame();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer player = manager.getGamePlayer(thrower);
    if (player instanceof final Survivor survivor) {
      survivor.setHasCarPart(false);
    }
  }
}
