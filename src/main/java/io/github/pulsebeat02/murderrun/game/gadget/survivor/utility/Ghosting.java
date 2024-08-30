package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Ghosting extends SurvivorGadget {

  public Ghosting() {
    super(
        "ghosting",
        Material.WHITE_WOOL,
        Message.GHOSTING_NAME.build(),
        Message.GHOSTING_LORE.build(),
        96);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game,
      final GamePlayer player,
      final org.bukkit.entity.Item item,
      final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    if (!(player instanceof final Survivor survivor)) {
      return true;
    }

    final DeathManager deathManager = player.getDeathManager();
    final PlayerDeathTask task =
        new PlayerDeathTask(() -> this.handleGhosting(game, survivor), false);
    deathManager.addDeathTask(task);

    final PlayerAudience audience = survivor.getAudience();
    final Component message = Message.GHOSTING_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GadgetConstants.GHOSTING_SOUND);

    return false;
  }

  private void handleGhosting(final Game game, final Survivor gamePlayer) {
    this.setPlayerAttributes(gamePlayer);
    this.giveWhiteLeatherArmor(gamePlayer);
    this.giveWhiteBone(gamePlayer);
    this.createWoolSetting(game, gamePlayer);
    this.teleport(game, gamePlayer);
  }

  private void teleport(final Game game, final GamePlayer gamePlayer) {
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location location = arena.getSpawn();
    gamePlayer.setRespawnLocation(location, true);
  }

  private void setPlayerAttributes(final Survivor gamePlayer) {
    gamePlayer.clearInventory();
    gamePlayer.setGameMode(GameMode.SURVIVAL);
    gamePlayer.setCanPickupCarPart(false);
    gamePlayer.setCanPlaceBlocks(true);
  }

  private void createWoolSetting(final Game game, final GamePlayer player) {
    final GameScheduler scheduler = game.getScheduler();
    final PlayerInventory inventory = player.getInventory();
    final ItemStack wool = Item.create(Material.WHITE_WOOL);
    scheduler.scheduleRepeatedTask(
        () -> inventory.addItem(wool), 0, GadgetConstants.GHOSTING_WOOL_DELAY);
  }

  private void giveWhiteBone(final GamePlayer player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = ItemFactory.createKnockBackBone();
    inventory.addItem(stack);
  }

  private void giveWhiteLeatherArmor(final GamePlayer player) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(ItemFactory.createGhostGear(Material.LEATHER_HELMET));
    inventory.setChestplate(ItemFactory.createGhostGear(Material.LEATHER_CHESTPLATE));
    inventory.setLeggings(ItemFactory.createGhostGear(Material.LEATHER_LEGGINGS));
    inventory.setBoots(ItemFactory.createGhostGear(Material.LEATHER_BOOTS));
  }
}
