package io.github.pulsebeat02.murderrun.player.death;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.papermc.paper.math.Rotations;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.title.Title.title;

public final class PlayerDeathManager {

  private final MurderGame game;
  private final ScheduledExecutorService service;

  public PlayerDeathManager(final MurderGame game) {
    this.game = game;
    this.service = Executors.newScheduledThreadPool(8);
  }

  public void shutdownExecutor() {
    this.service.shutdown();
  }

  private void setGameMode(final Player player) {
    player.setGameMode(GameMode.SPECTATOR);
  }

  private ArmorStand summonArmorStand(final Player player) {
    final Location location = player.getLocation();
    final World world = location.getWorld();
    final Entity entity = world.spawnEntity(location, EntityType.ARMOR_STAND);
    return (ArmorStand) entity;
  }

  private ItemStack getHeadItemStack(final Player player) {
    final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    final SkullMeta meta = (SkullMeta) head.getItemMeta();
    meta.setOwningPlayer(player);
    head.setItemMeta(meta);
    return head;
  }

  private void setArmorStandRotations(final ArmorStand stand) {
    stand.setHeadRotations(Rotations.ofDegrees(306, 0, 0));
    stand.setBodyRotations(Rotations.ofDegrees(283, 0, 0));
    stand.setLeftArmRotations(Rotations.ofDegrees(250, 0, 0));
    stand.setRightArmRotations(Rotations.ofDegrees(250, 360, 30));
  }

  private void setArmorStandGear(final Player player, final ArmorStand stand) {
    final ItemStack head = this.getHeadItemStack(player);
    final ItemStack chest = createArmorPiece(Material.LEATHER_CHESTPLATE);
    final ItemStack legs = createArmorPiece(Material.LEATHER_LEGGINGS);
    final ItemStack boots = createArmorPiece(Material.LEATHER_BOOTS);
    stand.setItem(EquipmentSlot.HEAD, head);
    stand.setItem(EquipmentSlot.BODY, chest);
    stand.setItem(EquipmentSlot.LEGS, legs);
    stand.setItem(EquipmentSlot.FEET, boots);
  }

  private void announcePlayerDeath(final Player dead) {
    final String name = dead.getName();
    final Component title = Locale.PLAYER_DEATH.build(name);
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllParticipants(this.game, title, subtitle);
  }

  public void initiateDeathSequence(final GamePlayer gamePlayer) {

    final Player player = gamePlayer.getPlayer();
    this.setGameMode(player);

    final ArmorStand stand = this.summonArmorStand(player);
    this.setArmorStandRotations(stand);
    this.setArmorStandGear(player, stand);
    this.announcePlayerDeath(player);

   // TODO: Summon car parts when player dies
  }

  public void spawnParticles() {
    final PlayerManager manager = this.game.getPlayerManager();
    this.service.scheduleAtFixedRate(
        () -> manager.getDead().forEach(this::spawnParticleOnCorpse), 0, 1, TimeUnit.SECONDS);
  }

  private void spawnParticleOnCorpse(final GamePlayer gamePlayer) {
    final Player player = gamePlayer.getPlayer();
    final Location location = player.getLastDeathLocation();
    final Location clone = location.clone().add(0, 1, 0);
    final World world = clone.getWorld();
    final BlockData data = Material.RED_CONCRETE.createBlockData();
    world.spawnParticle(Particle.BLOCK, clone, 10, 0.5, 0.5, 0.5, data);
  }

  public static ItemStack createArmorPiece(final Material leatherPiece) {
    final ItemStack item = new ItemStack(leatherPiece);
    final LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
    meta.setColor(Color.RED);
    item.setItemMeta(meta);
    return item;
  }
}
