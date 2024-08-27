package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Dormagogg extends KillerGadget implements Listener {

  private final Game game;

  public Dormagogg(final Game game) {
    super(
        "dormagogg",
        Material.WITHER_SKELETON_SKULL,
        Message.DORMAGOGG_NAME.build(),
        Message.DORMAGOGG_LORE.build(),
        16);
    this.game = game;
  }

  @EventHandler
  public void onTargetChange(final EntityTargetEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Zombie zombie)) {
      return;
    }

    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    final String target = container.get(Keys.DORMAGOGG_OWNER, PersistentDataType.STRING);
    final UUID uuid = UUID.fromString(target);
    final PlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(uuid)) {
      entity.remove();
      return;
    }

    final Location location = entity.getLocation();
    final GamePlayer nearest = manager.getNearestSurvivor(location);
    if (nearest == null) {
      entity.remove();
      return;
    }

    event.setCancelled(true);
    nearest.apply(zombie::setTarget);
  }

  @EventHandler
  public void onEntityDamage(final EntityDamageByEntityEvent event) {

    final Entity entity = event.getDamager();
    if (!(entity instanceof final Zombie zombie)) {
      return;
    }

    final Entity attacked = event.getEntity();
    if (!(attacked instanceof final Player player)) {
      return;
    }

    final PlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    final String owner = container.get(Keys.DORMAGOGG_OWNER, PersistentDataType.STRING);
    if (owner == null) {
      return;
    }
    zombie.remove();

    final UUID ownerUuid = UUID.fromString(owner);
    if (!manager.checkPlayerExists(ownerUuid)) {
      return;
    }

    final GamePlayer nearest = manager.getGamePlayer(player);
    final GamePlayer killer = manager.getGamePlayer(ownerUuid);
    final Game game = manager.getGame();
    final GameScheduler scheduler = game.getScheduler();
    nearest.disableJump(scheduler, 7 * 20L);
    nearest.disableWalkWithFOVEffects(10 * 20);
    nearest.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 1));

    final MetadataManager metadata = killer.getMetadataManager();
    metadata.setEntityGlowing(scheduler, nearest, ChatColor.RED, 7 * 20L);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GamePlayer nearest = manager.getNearestSurvivor(location);
    final GamePlayer killer = manager.getGamePlayer(player);
    if (nearest == null) {
      return;
    }

    this.spawnDormagogg(world, location, killer, nearest);
  }

  private void spawnDormagogg(
      final World world,
      final Location location,
      final GamePlayer killer,
      final GamePlayer nearest) {
    world.spawn(location, Zombie.class, zombie -> {
      this.customizeAttributes(zombie);
      this.setTargetMetadata(killer, zombie);
      this.setEquipment(zombie);
      if (zombie instanceof final Ageable ageable) {
        ageable.setBaby();
      }
    });
  }

  private void customizeAttributes(final Zombie zombie) {
    zombie.setInvulnerable(true);
    zombie.addPotionEffect(
        new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2));
    if (zombie instanceof final Ageable ageable) {
      ageable.setBaby();
    }
  }

  private void setTargetMetadata(final GamePlayer killer, final Zombie zombie) {
    final UUID killerUuid = killer.getUUID();
    final String killerData = killerUuid.toString();
    final PersistentDataContainer container = zombie.getPersistentDataContainer();
    container.set(Keys.DORMAGOGG_OWNER, PersistentDataType.STRING, killerData);
  }

  private void setEquipment(final Zombie zombie) {
    final EntityEquipment equipment = requireNonNull(zombie.getEquipment());
    equipment.setHelmet(Item.create(Material.WITHER_SKELETON_SKULL));
    equipment.setChestplate(Item.create(Material.NETHERITE_CHESTPLATE));
    equipment.setLeggings(Item.create(Material.NETHERITE_LEGGINGS));
    equipment.setBoots(Item.create(Material.NETHERITE_BOOTS));
  }
}
