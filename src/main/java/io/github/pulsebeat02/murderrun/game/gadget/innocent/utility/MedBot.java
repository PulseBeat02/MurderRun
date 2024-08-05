package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class MedBot extends MurderGadget {

  public MedBot() {
    super(
        "med_bot",
        Material.CHORUS_FLOWER,
        Locale.MED_BOT_TRAP_NAME.build(),
        Locale.MED_BOT_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final MurderPlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    final ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
    armorStand.setInvisible(true);
    armorStand.setGravity(false);
    armorStand.setMarker(true);

    final EntityEquipment equipment = armorStand.getEquipment();
    if (equipment == null) {
      throw new AssertionError("Failed to spawn Med Bot!");
    }
    equipment.setHelmet(new ItemStack(Material.CHORUS_FLOWER));

    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTaskUntilCondition(
        () -> this.handleArmorStandEffects(armorStand), 0, 2, armorStand::isDead);
    scheduler.scheduleTaskUntilCondition(
        () -> manager.applyToAllInnocents(
            innocent -> this.handleInnocentEffects(innocent, armorStand)),
        40L,
        20L,
        armorStand::isDead);
    scheduler.scheduleTaskUntilCondition(
        () ->
            manager.applyToAllInnocents(innocent -> this.handleKillerDestroy(innocent, armorStand)),
        40L,
        20L,
        armorStand::isDead);
  }

  private void handleKillerDestroy(final GamePlayer killer, final ArmorStand stand) {
    final Location origin = stand.getLocation();
    final Location location = killer.getLocation();
    final double distance = origin.distanceSquared(location);
    if (distance <= 1) {
      stand.remove();
    }
  }

  private void handleInnocentEffects(final GamePlayer innocent, final ArmorStand stand) {
    final Location origin = stand.getLocation();
    final Location location = innocent.getLocation();
    final double distance = origin.distanceSquared(location);
    if (distance <= 64) {
      innocent.addPotionEffects(new PotionEffect(PotionEffectType.REGENERATION, 5, 2));
    }
  }

  private void handleArmorStandEffects(final ArmorStand stand) {
    this.handleRotation(stand);
    this.handleVerticalBobbing(stand);
    this.handleParticles(stand);
  }

  private void handleParticles(final ArmorStand stand) {

    final Location location = stand.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    world.spawnParticle(Particle.ENTITY_EFFECT, location, 5, 8, 8, 8, Color.PINK);
  }

  private void handleVerticalBobbing(final ArmorStand stand) {
    final Location location = stand.getLocation();
    location.add(0, Math.sin(System.currentTimeMillis() / 1000.0) * 0.1, 0);
    stand.teleport(location);
  }

  private void handleRotation(final ArmorStand stand) {
    final Location location = stand.getLocation();
    final float yaw = location.getYaw();
    final float pitch = location.getPitch();
    stand.setRotation(yaw, pitch);
  }
}
