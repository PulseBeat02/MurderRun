package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;

public final class MedBot extends SurvivorGadget {

  public MedBot() {
    super(
      "med_bot",
      Material.DISPENSER,
      Message.MED_BOT_NAME.build(),
      Message.MED_BOT_LORE.build(),
      GameProperties.MED_BOT_COST
    );
  }

  @Override
  public boolean onGadgetDrop(
    final Game game,
    final GamePlayer player,
    final org.bukkit.entity.Item item,
    final boolean remove
  ) {
    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
    armorStand.setInvisible(true);
    armorStand.setGravity(false);
    armorStand.setMarker(true);

    final EntityEquipment equipment = requireNonNull(armorStand.getEquipment());
    equipment.setHelmet(Item.create(Material.CHORUS_FLOWER));

    final GameScheduler scheduler = game.getScheduler();
    this.handleRotation(scheduler, armorStand);
    this.handleVerticalMotion(scheduler, armorStand);
    this.handleParticles(scheduler, armorStand);
    this.handleMedBotUpdate(scheduler, manager, armorStand);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.MED_BOT_SOUND);

    return false;
  }

  private void handleMedBotUpdate(final GameScheduler scheduler, final PlayerManager manager, final ArmorStand stand) {
    final Consumer<GamePlayer> consumer = survivor -> this.handleInnocentEffects(survivor, stand);
    final Consumer<GamePlayer> killerConsumer = killer -> this.handleKillerDestroy(manager, killer, stand);
    final Runnable task = () -> {
      manager.applyToAllLivingInnocents(consumer);
      manager.applyToAllMurderers(killerConsumer);
    };
    scheduler.scheduleConditionalTask(task, 0, 5L, stand::isDead);
  }

  private void handleKillerDestroy(final PlayerManager manager, final GamePlayer killer, final ArmorStand stand) {
    final Location origin = stand.getLocation();
    final Location location = killer.getLocation();
    final double distance = origin.distanceSquared(location);
    final double radius = GameProperties.MED_BOT_DESTROY_RADIUS;
    if (distance < radius * radius) {
      final Component message = Message.MED_BOT_DEACTIVATE.build();
      manager.sendMessageToAllSurvivors(message);
      stand.remove();
    }
  }

  private void handleInnocentEffects(final GamePlayer innocent, final ArmorStand stand) {
    final Location origin = stand.getLocation();
    final Location location = innocent.getLocation();
    final double distance = origin.distanceSquared(location);
    final double radius = GameProperties.MED_BOT_RADIUS;
    if (distance < radius * radius) {
      innocent.addPotionEffects(new PotionEffect(PotionEffectType.REGENERATION, 2 * 20, 3));
    }
  }

  private void handleRotation(final GameScheduler scheduler, final ArmorStand stand) {
    scheduler.scheduleConditionalTask(() -> this.rotateOneIteration(stand), 0, 1, stand::isDead);
  }

  private void handleVerticalMotion(final GameScheduler scheduler, final ArmorStand stand) {
    final AtomicDouble lastYOffset = new AtomicDouble();
    final AtomicLong currentTick = new AtomicLong();
    final Runnable task = () ->
      lastYOffset.set(this.moveVerticallyOneIteration(stand, currentTick.getAndIncrement(), lastYOffset.get()));
    scheduler.scheduleConditionalTask(task, 0, 1, stand::isDead);
  }

  private void handleParticles(final GameScheduler scheduler, final ArmorStand stand) {
    scheduler.scheduleConditionalTask(() -> this.spawnParticle(stand), 0, 2, stand::isDead);
  }

  private void spawnParticle(final ArmorStand stand) {
    final Location location = stand.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 5, 8, 8, 8, new DustOptions(Color.PURPLE, 4));
  }

  private double moveVerticallyOneIteration(final ArmorStand stand, final long current, final double lastYOffset) {
    final double yOffset = Math.sin(Math.toRadians(current * 5.0d));
    stand.teleport(stand.getLocation().add(0, (yOffset - lastYOffset), 0));
    return yOffset;
  }

  private void rotateOneIteration(final ArmorStand stand) {
    final EulerAngle angle = stand.getHeadPose();
    final EulerAngle result = angle.add(0, 0.1, 0);
    stand.setHeadPose(result);
  }
}
