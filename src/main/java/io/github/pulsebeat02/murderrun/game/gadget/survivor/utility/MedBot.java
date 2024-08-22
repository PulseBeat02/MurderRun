package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class MedBot extends SurvivorGadget {

  public MedBot() {
    super(
        "med_bot",
        Material.DISPENSER,
        Message.MED_BOT_NAME.build(),
        Message.MED_BOT_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
    armorStand.setInvisible(true);
    armorStand.setGravity(false);
    armorStand.setMarker(true);

    final EntityEquipment equipment = requireNonNull(armorStand.getEquipment());
    equipment.setHelmet(Item.create(Material.CHORUS_FLOWER));

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTaskUntilCondition(
        () -> this.handleArmorStandEffects(armorStand), 0, 2, armorStand::isDead);
    scheduler.scheduleTaskUntilCondition(
        () -> this.handleMedBotUpdate(manager, armorStand), 0, 20L, armorStand::isDead);
  }

  private void handleMedBotUpdate(final PlayerManager manager, final ArmorStand stand) {
    manager.applyToAllLivingInnocents(innocent -> {
      this.handleInnocentEffects(innocent, stand);
      this.handleKillerDestroy(manager, innocent, stand);
    });
  }

  private void handleKillerDestroy(
      final PlayerManager manager, final GamePlayer killer, final ArmorStand stand) {
    final Location origin = stand.getLocation();
    final Location location = killer.getLocation();
    final double distance = origin.distanceSquared(location);
    if (distance < 1) {
      final Component message = Message.MED_BOT_DEACTIVATE.build();
      manager.applyToAllLivingInnocents(innocent -> innocent.sendMessage(message));
      stand.remove();
    }
  }

  private void handleInnocentEffects(final GamePlayer innocent, final ArmorStand stand) {
    final Location origin = stand.getLocation();
    final Location location = innocent.getLocation();
    final double distance = origin.distanceSquared(location);
    if (distance < 64) {
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
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 5, 8, 8, 8, new DustOptions(Color.PURPLE, 4));
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
