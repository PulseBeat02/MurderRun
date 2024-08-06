package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import io.github.pulsebeat02.murderrun.game.arena.MurderArena;
import io.github.pulsebeat02.murderrun.game.gadget.DeathTask;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
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
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class Ghosting extends MurderGadget {

  public Ghosting() {
    super(
        "ghosting",
        Material.WHITE_WOOL,
        Locale.GHOSTING_TRAP_NAME.build(),
        Locale.GHOSTING_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final MurderPlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final DeathTask task = new DeathTask(() -> this.handleGhosting(game, gamePlayer), false);
    gamePlayer.addDeathTask(task);

    final Component message = Locale.GHOSTING_TRAP_ACTIVATE.build();
    gamePlayer.sendMessage(message);
  }

  private void handleGhosting(final MurderGame game, final GamePlayer gamePlayer) {
    this.setPlayerAttributes(gamePlayer);
    this.giveWhiteLeatherArmor(gamePlayer);
    this.giveWhiteBone(gamePlayer);
    this.createWoolSetting(game, gamePlayer);
    this.teleport(game, gamePlayer);
  }

  private void teleport(final MurderGame game, final GamePlayer gamePlayer) {
    final MurderSettings settings = game.getSettings();
    final MurderArena arena = settings.getArena();
    final Location location = arena.getSpawn();
    gamePlayer.teleport(location);
  }

  private void setPlayerAttributes(final GamePlayer gamePlayer) {
    final PlayerInventory inventory = gamePlayer.getInventory();
    inventory.clear();
    gamePlayer.apply(player -> player.setGameMode(GameMode.SURVIVAL));
  }

  private void createWoolSetting(final MurderGame game, final GamePlayer player) {
    final MurderGameScheduler scheduler = game.getScheduler();
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
    inventory.setHelmet(createArmorPiece(Material.LEATHER_HELMET));
    inventory.setChestplate(createArmorPiece(Material.LEATHER_CHESTPLATE));
    inventory.setLeggings(createArmorPiece(Material.LEATHER_LEGGINGS));
    inventory.setBoots(createArmorPiece(Material.LEATHER_BOOTS));
  }

  public static ItemStack createArmorPiece(final Material leatherPiece) {
    final ItemStack item = new ItemStack(leatherPiece);
    final LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
    if (meta == null) {
      throw new AssertionError("Failed to dye leather armor!");
    }
    meta.setColor(Color.WHITE);
    item.setItemMeta(meta);
    return item;
  }
}
