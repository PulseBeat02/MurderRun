package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class Ghosting extends SurvivorGadget {

  public Ghosting() {
    super(
        "ghosting",
        Material.WHITE_WOOL,
        Locale.GHOSTING_TRAP_NAME.build(),
        Locale.GHOSTING_TRAP_LORE.build(),
        96);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final PlayerDeathTask task =
        new PlayerDeathTask(() -> this.handleGhosting(game, gamePlayer), false);
    gamePlayer.addDeathTask(task);

    final Component message = Locale.GHOSTING_TRAP_ACTIVATE.build();
    gamePlayer.sendMessage(message);
  }

  private void handleGhosting(final Game game, final GamePlayer gamePlayer) {
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
    gamePlayer.teleport(location);
  }

  private void setPlayerAttributes(final GamePlayer gamePlayer) {
    final PlayerInventory inventory = gamePlayer.getInventory();
    inventory.clear();
    gamePlayer.apply(player -> player.setGameMode(GameMode.SURVIVAL));
  }

  private void createWoolSetting(final Game game, final GamePlayer player) {
    final GameScheduler scheduler = game.getScheduler();
    final PlayerInventory inventory = player.getInventory();
    final ItemStack wool = new ItemStack(Material.WHITE_WOOL);
    scheduler.scheduleRepeatedTask(() -> inventory.addItem(wool), 0, 100);
  }

  private void giveWhiteBone(final GamePlayer player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack stack = new ItemStack(Material.BONE);
    stack.addEnchantment(Enchantment.KNOCKBACK, 2);
    inventory.addItem(stack);
  }

  private void giveWhiteLeatherArmor(final GamePlayer player) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(this.createArmorPiece(Material.LEATHER_HELMET));
    inventory.setChestplate(this.createArmorPiece(Material.LEATHER_CHESTPLATE));
    inventory.setLeggings(this.createArmorPiece(Material.LEATHER_LEGGINGS));
    inventory.setBoots(this.createArmorPiece(Material.LEATHER_BOOTS));
  }

  private ItemStack createArmorPiece(final Material leatherPiece) {
    final ItemStack item = new ItemStack(leatherPiece);
    final ItemMeta meta = requireNonNull(item.getItemMeta());
    if (meta instanceof final LeatherArmorMeta leatherArmorMeta) {
      leatherArmorMeta.setColor(Color.WHITE);
      item.setItemMeta(leatherArmorMeta);
    }
    return item;
  }
}
