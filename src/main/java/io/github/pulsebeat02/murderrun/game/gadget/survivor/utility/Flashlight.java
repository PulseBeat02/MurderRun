package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class Flashlight extends SurvivorGadget {

  public Flashlight() {
    super(
        "flashlight",
        Material.GOLDEN_SHOVEL,
        Message.FLASHLIGHT_NAME.build(),
        Message.FLASHLIGHT_LORE.build(),
        48,
        stack -> PDCUtils.setPersistentDataAttribute(
            stack, Keys.FLASH_LIGHT_LAST_USE, PersistentDataType.LONG, 0L));
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, false);
  }

  @Override
  public void onGadgetRightClick(
      final Game game, final PlayerInteractEvent event, final boolean remove) {

    super.onGadgetRightClick(game, event, false);

    final ItemStack stack = event.getItem();
    if (stack == null) {
      return;
    }

    final Long last = requireNonNull(PDCUtils.getPersistentDataAttribute(
        stack, Keys.FLASH_LIGHT_LAST_USE, PersistentDataType.LONG));
    final long current = System.currentTimeMillis();
    if (current - last < 5000) {
      return;
    }

    final Player player = event.getPlayer();
    PDCUtils.setPersistentDataAttribute(
        stack, Keys.FLASH_LIGHT_LAST_USE, PersistentDataType.LONG, current);
    this.sprayParticlesInCone(game, player);

    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final PlayerAudience audience = gamePlayer.getAudience();
    audience.playSound(Sounds.FLASHLIGHT);
  }

  private void sprayParticlesInCone(final Game game, final Player player) {

    final PlayerManager manager = game.getPlayerManager();
    final Location handLocation = player.getEyeLocation();
    final Vector direction = handLocation.getDirection();
    final double coneAngle = Math.toRadians(30);
    final double coneLength = 5;
    final double increment = Math.toRadians(5);

    for (double t = 0; t < coneLength; t += 0.5) {
      for (double angle = -coneAngle; angle <= coneAngle; angle += increment) {

        final Vector copy = direction.clone();
        final Vector offset = copy.multiply(t);
        offset.rotateAroundY(angle);

        final Location hand = handLocation.clone();
        final Location particleLocation = hand.add(offset);
        final World world = requireNonNull(hand.getWorld());
        world.spawnParticle(
            Particle.DUST, particleLocation, 1, 0, 0, 0, 0, new DustOptions(Color.YELLOW, 3));
        manager.applyToAllMurderers(killer -> applyPotionEffects(killer, particleLocation));
      }
    }
  }

  private static void applyPotionEffects(final GamePlayer killer, final Location particleLocation) {
    final Location killerLocation = killer.getLocation();
    final double distance = killerLocation.distanceSquared(particleLocation);
    if (distance < 4) {
      killer.addPotionEffects(
          new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, Integer.MAX_VALUE));
    }
  }
}
