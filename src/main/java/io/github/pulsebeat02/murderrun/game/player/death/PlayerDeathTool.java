package io.github.pulsebeat02.murderrun.game.player.death;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
    final Location down = location.subtract(0, 1.5, 0);
    final World world = requireNonNull(down.getWorld());
    final Entity entity = world.spawnEntity(down, EntityType.ARMOR_STAND);
    return (ArmorStand) entity;
  }

  private void customizeArmorStand(final ArmorStand stand) {
    stand.setInvulnerable(true);
    stand.setGravity(false);
    stand.setBasePlate(false);
  }

  private void setArmorStandRotations(final ArmorStand stand) {
    stand.setHeadPose(MapUtils.toEulerAngle(90, 0, 0));
    stand.setBodyPose(MapUtils.toEulerAngle(90, 0, 0));
    stand.setLeftArmPose(MapUtils.toEulerAngle(90, 0, 0));
    stand.setRightArmPose(MapUtils.toEulerAngle(90, 0, 0));
    stand.setLeftLegPose(MapUtils.toEulerAngle(90, 0, 0));
    stand.setRightLegPose(MapUtils.toEulerAngle(90, 0, 0));
  }

  private void setArmorStandGear(final Player player, final ArmorStand stand) {
    final EntityEquipment equipment = requireNonNull(stand.getEquipment());
    final ItemStack head = ItemFactory.createPlayerHead(player);
    final ItemStack chest = ItemFactory.createDeathGear(Material.LEATHER_CHESTPLATE);
    final ItemStack legs = ItemFactory.createDeathGear(Material.LEATHER_LEGGINGS);
    final ItemStack boots = ItemFactory.createDeathGear(Material.LEATHER_BOOTS);
    equipment.setHelmet(head);
    equipment.setChestplate(chest);
    equipment.setLeggings(legs);
    equipment.setBoots(boots);
  }

  private void announcePlayerDeath(final Player dead) {
    final String name = dead.getDisplayName();
    final Component title = Message.PLAYER_DEATH.build(name);
    final PlayerManager manager = this.game.getPlayerManager();
    manager.sendMessageToAllParticipants(title);
  }

  private void summonCarParts(final Player player) {
    final PlayerInventory inventory = player.getInventory();
    final ItemStack[] slots = inventory.getContents();
    for (final ItemStack slot : slots) {
      if (!PDCUtils.isCarPart(slot)) {
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

  public void spawnParticles() {
    final PlayerManager manager = this.game.getPlayerManager();
    final GameScheduler scheduler = this.game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllDead(this::spawnParticleOnCorpse), 0, 20L);
  }

  private void spawnParticleOnCorpse(final GamePlayer gamePlayer) {

    final ArmorStand stand = gamePlayer.getCorpse();
    if (stand == null) {
      return;
    }

    if (stand.isDead()) {
      gamePlayer.setCorpse(null);
      return;
    }

    final Location location = stand.getLocation();
    final Location clone = location.clone().add(0, 1, 0);
    final World world = requireNonNull(clone.getWorld());
    world.spawnParticle(Particle.DUST, clone, 10, 0.5, 0.5, 0.5, new DustOptions(Color.RED, 4));
  }
}
