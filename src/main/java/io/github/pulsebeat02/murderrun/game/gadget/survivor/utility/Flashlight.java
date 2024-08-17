package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
        stack -> ItemUtils.setPersistentDataAttribute(
            stack, Keys.FLASH_LIGHT_LAST_USE, PersistentDataType.LONG, 0L));
  }

  @Override
  public void onGadgetRightClick(
      final Game game, final PlayerInteractEvent event, final boolean remove) {

    super.onGadgetRightClick(game, event, false);

    final ItemStack stack = event.getItem();
    if (stack == null) {
      return;
    }

    final Long last = requireNonNull(ItemUtils.getPersistentDataAttribute(
        stack, Keys.FLASH_LIGHT_LAST_USE, PersistentDataType.LONG));
    final long current = System.currentTimeMillis();
    if (current - last < 5000) {
      return;
    }

    final Player player = event.getPlayer();
    ItemUtils.setPersistentDataAttribute(
        stack, Keys.FLASH_LIGHT_LAST_USE, PersistentDataType.LONG, current);
    this.sprayParticlesInCone(game, player);
  }

  private void sprayParticlesInCone(final Game game, final Player player) {

    final PlayerManager manager = game.getPlayerManager();
    final Location handLocation = player.getEyeLocation();
    final Vector direction = handLocation.getDirection();
    final double coneAngle = Math.toRadians(30);
    final double coneLength = 5;

    for (double t = 0; t < coneLength; t += 0.5) {
      for (double angle = -coneAngle; angle <= coneAngle; angle += Math.toRadians(5)) {

        final Vector copy = direction.clone();
        final Vector offset = copy.multiply(t);
        offset.rotateAroundY(angle);

        final Location hand = handLocation.clone();
        final Location particleLocation = hand.add(offset);
        final World world = requireNonNull(hand.getWorld());
        world.spawnParticle(Particle.SMOKE, particleLocation, 1, 0, 0, 0, 0);
        manager.applyToAllMurderers(killer -> applyPotionEffects(killer, particleLocation));
      }
    }
  }

  private static void applyPotionEffects(final GamePlayer killer, final Location particleLocation) {
    final Location killerLocation = killer.getLocation();
    final double distance = killerLocation.distanceSquared(particleLocation);
    if (distance < 1) {
      killer.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, 10, 0));
    }
  }
}
