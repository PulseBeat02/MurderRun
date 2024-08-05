package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.ItemStackUtils;
import io.github.pulsebeat02.murderrun.utils.NamespacedKeys;
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

public final class Flashlight extends MurderGadget {

  public Flashlight() {
    super(
        "flashlight",
        Material.GOLDEN_SHOVEL,
        Locale.FLASHLIGHT_TRAP_NAME.build(),
        Locale.FLASHLIGHT_TRAP_LORE.build(),
        stack -> ItemStackUtils.setData(
            stack, NamespacedKeys.FLASH_LIGHT_LAST_USE, PersistentDataType.LONG, 0L));
  }

  @Override
  public void onGadgetRightClick(
      final MurderGame game, final PlayerInteractEvent event, final boolean remove) {

    super.onGadgetRightClick(game, event, false);

    final ItemStack stack = event.getItem();
    final Long last =
        ItemStackUtils.getData(stack, NamespacedKeys.FLASH_LIGHT_LAST_USE, PersistentDataType.LONG);
    if (last == null) {
      throw new AssertionError("Failed to get last use of flashlight!");
    }

    final long current = System.currentTimeMillis();
    if (current - last < 5000) {
      return;
    }

    final Player player = event.getPlayer();
    ItemStackUtils.setData(
        stack, NamespacedKeys.FLASH_LIGHT_LAST_USE, PersistentDataType.LONG, current);
    this.sprayParticlesInCone(game, player);
  }

  public void sprayParticlesInCone(final MurderGame game, final Player player) {

    final MurderPlayerManager manager = game.getPlayerManager();
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
        final World world = hand.getWorld();
        if (world == null) {
          throw new AssertionError("Location doesn't have World attached to it!");
        }

        world.spawnParticle(Particle.SMOKE, particleLocation, 1, 0, 0, 0, 0);
        manager.applyToAllMurderers(killer -> applyPotionEffects(killer, particleLocation));
      }
    }
  }

  private static void applyPotionEffects(final GamePlayer killer, final Location particleLocation) {
    final Location killerLocation = killer.getLocation();
    final double distance = killerLocation.distanceSquared(particleLocation);
    if (distance <= 1) {
      killer.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, 10, 0));
    }
  }
}
