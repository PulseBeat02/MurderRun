package io.github.pulsebeat02.murderrun.map.event;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import io.github.pulsebeat02.murderrun.game.MurderWinCode;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.map.MurderMap;
import io.github.pulsebeat02.murderrun.map.part.CarPartItemStack;
import io.github.pulsebeat02.murderrun.map.part.CarPartManager;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.InnocentPlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.ItemStackUtils;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class GamePlayerThrowCarPartEvent implements Listener {

  private final MurderGame game;

  public GamePlayerThrowCarPartEvent(final MurderGame game) {
    this.game = game;
  }

  public MurderGame getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerThrowItem(final PlayerDropItemEvent event) {

    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    if (!ItemStackUtils.isCarPart(stack)) {
      return;
    }

    final MurderSettings configuration = this.game.getSettings();
    final MurderArena arena = configuration.getArena();
    final Location truckLocation = arena.getTruck();
    final Location itemLocation = item.getLocation();
    final double distSquared = itemLocation.distanceSquared(truckLocation);
    if (distSquared > 16) {
      return;
    }

    item.remove();

    final MurderMap map = this.game.getMurderMap();
    final CarPartManager manager = map.getCarPartManager();
    final CarPartItemStack carPartItemStack = manager.getCarPartItemStack(stack);
    final Map<String, CarPartItemStack> carPartItemStackMap = manager.getParts();
    manager.removeCarPart(carPartItemStack);

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
    final Component title = Locale.CAR_PART_ITEM_RETRIEVAL.build(leftOver);
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllParticipants(this.game, title, subtitle);
    AdventureUtils.playSoundForAllParticipants(this.game, Sound.BLOCK_ANVIL_USE);
  }

  private void setBossBar(final int leftOver) {
    final MurderSettings settings = this.game.getSettings();
    final int parts = settings.getCarPartCount();
    final int collected = parts - leftOver;
    final Component name = Locale.BOSS_BAR.build(collected, parts);
    final float progress = 0f;
    final BossBar.Color color = BossBar.Color.GREEN;
    final BossBar.Overlay overlay = BossBar.Overlay.NOTCHED_20;
    AdventureUtils.showBossBarForAllParticipants(this.game, name, progress, color, overlay);
  }

  private void checkGameEnd(final int leftOver) {
    if (leftOver == 0) {
      this.game.finishGame(MurderWinCode.INNOCENTS);
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

  private void setPlayerCarPartStatus(final Player thrower) {
    final PlayerManager manager = this.game.getPlayerManager();
    final UUID uuid = thrower.getUniqueId();
    final GamePlayer player = manager.lookupPlayer(uuid).orElseThrow();
    if (player instanceof final InnocentPlayer innocent) {
      innocent.setHasCarPart(false);
    }
  }
}
