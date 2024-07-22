package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.MurderGame;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.papermc.paper.math.Rotations;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.title.Title.title;

public sealed class GamePlayer permits InnocentPlayer, Murderer {

  private final MurderGame game;
  private final UUID uuid;
  private boolean alive;

  public GamePlayer(final MurderGame game, final UUID uuid) {
    this.game = game;
    this.uuid = uuid;
    this.alive = true;
  }

  public void markDeath() {
    this.setAlive(false);
    final Player player = this.getPlayer();
    final Location location = player.getLocation();
    player.setGameMode(GameMode.SPECTATOR);
    final Entity entity = Bukkit.getWorld("world").spawnEntity(location, EntityType.ARMOR_STAND);
    final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    final SkullMeta meta = (SkullMeta) head.getItemMeta();
    meta.setOwningPlayer(player);
    head.setItemMeta(meta);
    final ArmorStand stand = (ArmorStand) entity;
    stand.setHeadRotations(Rotations.ofDegrees(306, 0, 0));
    stand.setBodyRotations(Rotations.ofDegrees(283, 0, 0));
    stand.setLeftArmRotations(Rotations.ofDegrees(250, 0, 0));
    stand.setRightArmRotations(Rotations.ofDegrees(250, 360, 30));
    stand.setItem(EquipmentSlot.HEAD, head);
    stand.setItem(EquipmentSlot.BODY, createArmorPiece(Material.LEATHER_CHESTPLATE));
    stand.setItem(EquipmentSlot.LEGS, createArmorPiece(Material.LEATHER_LEGGINGS));
    stand.setItem(EquipmentSlot.FEET, createArmorPiece(Material.LEATHER_BOOTS));
    final Plugin plugin = MurderRun.getPlugin(MurderRun.class);
    Bukkit.getScheduler()
        .runTaskTimer(plugin, (task) -> this.spawnParticles(task, location), 0, 20);
    final PlayerManager manager = this.game.getPlayerManager();
    for (final Player pl : manager.getParticipants()) {
      pl.showTitle(title(Locale.PLAYER_DEATH.build(player.getName()), empty()));
    }
  }

  public void spawnParticles(final BukkitTask task, final Location location) {
    if (this.alive) {
      task.cancel();
    }
    final World world = location.getWorld();
    world.spawnParticle(
        Particle.BLOCK, location, 10, 0.5, 0.5, 0.5, Material.RED_CONCRETE.createBlockData());
  }

  public static ItemStack createArmorPiece(final Material leatherPiece) {
    final ItemStack item = new ItemStack(leatherPiece);
    final LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
    meta.setColor(Color.RED);
    item.setItemMeta(meta);
    return item;
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(this.uuid);
  }

  public boolean isAlive() {
    return this.alive;
  }

  public void setAlive(final boolean alive) {
    this.alive = alive;
  }
}
