package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.helper.TargetEntityInstance;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
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

  private static final String DORMAGOGG_SOUND = "entity.zombie.ambient";

  private final TargetEntityInstance target;

  public Dormagogg(final Game game) {
    super(
        "dormagogg",
        Material.WITHER_SKELETON_SKULL,
        Message.DORMAGOGG_NAME.build(),
        Message.DORMAGOGG_LORE.build(),
        16);
    this.target = new TargetEntityInstance(game);
  }

  @EventHandler
  public void onTargetChange(final EntityTargetEvent event) {
    this.target.onDormagoggTarget(event);
  }

  @EventHandler
  public void onEntityDamage(final EntityDamageByEntityEvent event) {
    this.target.onDormagoggDamage(event);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GamePlayer killer = manager.getGamePlayer(player);
    this.spawnDormagogg(world, location, killer);

    final PlayerAudience audience = killer.getAudience();
    audience.playSound(DORMAGOGG_SOUND);
  }

  private void spawnDormagogg(final World world, final Location location, final GamePlayer killer) {
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
