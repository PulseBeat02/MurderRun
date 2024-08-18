package io.github.pulsebeat02.murderrun.game.player.death;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class PlayerDeathTool {

  private final Game game;

  public PlayerDeathTool(final Game game) {
    this.game = game;
  }

  public Game getGame() {
    return this.game;
  }

  public void initiateDeathSequence(final GamePlayer gamePlayer) {
    gamePlayer.apply(player -> {
      final ArmorStand stand = this.summonArmorStand(gamePlayer);
      this.preparePlayer(player);
      this.customizeArmorStand(stand);
      this.setArmorStandRotations(stand);
      this.setArmorStandGear(player, stand);
      this.announcePlayerDeath(player);
      this.summonCarParts(player);
      gamePlayer.setCorpse(stand);
    });
  }

  private void preparePlayer(final Player player) {
    player.setGameMode(GameMode.SPECTATOR);
    final PlayerInventory inventory = player.getInventory();
    inventory.clear();
  }

  private ArmorStand summonArmorStand(final GamePlayer player) {
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final Entity entity = world.spawnEntity(location, EntityType.ARMOR_STAND);
    return (ArmorStand) entity;
  }

  private void customizeArmorStand(final ArmorStand stand) {
    stand.setInvulnerable(true);
    stand.setGravity(false);
  }

  private void setArmorStandRotations(final ArmorStand stand) {
    stand.setHeadPose(MapUtils.toEulerAngle(300, 0, 0));
    stand.setBodyPose(MapUtils.toEulerAngle(280, 0, 0));
    stand.setLeftArmPose(MapUtils.toEulerAngle(250, 0, 0));
    stand.setRightArmPose(MapUtils.toEulerAngle(250, 360, 30));
  }

  private void setArmorStandGear(final Player player, final ArmorStand stand) {
    final EntityEquipment equipment = requireNonNull(stand.getEquipment());
    final ItemStack head = this.getHeadItemStack(player);
    final ItemStack chest = createArmorPiece(Material.LEATHER_CHESTPLATE);
    final ItemStack legs = createArmorPiece(Material.LEATHER_LEGGINGS);
    final ItemStack boots = createArmorPiece(Material.LEATHER_BOOTS);
    equipment.setHelmet(head);
    equipment.setChestplate(chest);
    equipment.setLeggings(legs);
    equipment.setBoots(boots);
  }

  private void announcePlayerDeath(final Player dead) {
    final String name = dead.getDisplayName();
    final Component title = Message.PLAYER_DEATH.build(name);
    final Component subtitle = empty();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.showTitleForAllParticipants(title, subtitle);
  }

  private void summonCarParts(final Player player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack[] slots = inventory.getContents();
    for (final ItemStack slot : slots) {
      if (!ItemUtils.isCarPart(slot)) {
        continue;
      }
      final Map map = this.game.getMap();
      final PartsManager manager = map.getCarPartManager();
      final CarPart stack = requireNonNull(manager.getCarPartItemStack(slot));
      final Location death = requireNonNull(player.getLastDeathLocation());
      stack.setPickedUp(false);
      stack.setLocation(death);
      stack.spawn();
    }
  }

  private ItemStack getHeadItemStack(final Player player) {
    final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    final ItemMeta meta = requireNonNull(head.getItemMeta());
    if (meta instanceof final SkullMeta skullMeta) {
      skullMeta.setOwningPlayer(player);
      head.setItemMeta(skullMeta);
    }
    return head;
  }

  public static ItemStack createArmorPiece(final Material leatherPiece) {
    final ItemStack item = new ItemStack(leatherPiece);
    final ItemMeta meta = requireNonNull(item.getItemMeta());
    if (meta instanceof final LeatherArmorMeta leatherArmorMeta) {
      leatherArmorMeta.setColor(Color.RED);
      item.setItemMeta(leatherArmorMeta);
    }
    return item;
  }

  public void spawnParticles() {
    final PlayerManager manager = this.game.getPlayerManager();
    final GameScheduler scheduler = this.game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllDead(this::spawnParticleOnCorpse), 0, 20);
  }

  private void spawnParticleOnCorpse(final GamePlayer gamePlayer) {
    final Location location = gamePlayer.getDeathLocation();
    if (location == null) {
      throw new AssertionError("Player didn't die! Fake death error?");
    }
    final Location clone = location.clone().add(0, 1, 0);
    final World world = requireNonNull(clone.getWorld());
    final BlockData data = Material.RED_CONCRETE.createBlockData();
    world.spawnParticle(Particle.BLOCK, clone, 10, 0.5, 0.5, 0.5, data);
  }
}
