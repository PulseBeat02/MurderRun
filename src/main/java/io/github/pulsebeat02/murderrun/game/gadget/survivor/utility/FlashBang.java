package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

public final class FlashBang extends SurvivorGadget implements Listener {

  private final Game game;

  public FlashBang(final Game game) {
    super(
        "flash_bang",
        Material.SNOWBALL,
        Message.FLASHBANG_NAME.build(),
        Message.FLASHBANG_LORE.build(),
        8,
        stack -> ItemUtils.setPersistentDataAttribute(
            stack, Keys.FLASH_BANG, PersistentDataType.BOOLEAN, true));
    this.game = game;
  }

  @EventHandler
  public void onProjectileHitEvent(final ProjectileHitEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Snowball snowball)) {
      return;
    }

    final ItemStack stack = snowball.getItem();
    if (!ItemUtils.isFlashBang(stack)) {
      return;
    }

    final Block block = event.getHitBlock();
    if (block == null) {
      return;
    }

    final Location location = block.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.WHITE_ASH, location, 30, 1, 1, 1, 1);

    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllMurderers(killer -> {
      final Location killerLocation = killer.getLocation();
      final double distance = killerLocation.distanceSquared(location);
      if (distance < 1) {
        killer.addPotionEffects(
            PotionEffectType.BLINDNESS.createEffect(10, 0),
            PotionEffectType.SLOWNESS.createEffect(10, 1));
      }
    });
  }
}
