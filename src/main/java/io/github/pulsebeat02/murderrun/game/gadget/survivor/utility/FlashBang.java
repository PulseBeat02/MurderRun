package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
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
        stack -> PDCUtils.setPersistentDataAttribute(
            stack, Keys.FLASH_BANG, PersistentDataType.BOOLEAN, true));
    this.game = game;
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, false);
  }

  @EventHandler
  public void onProjectileHitEvent(final ProjectileHitEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Snowball snowball)) {
      return;
    }

    final ItemStack stack = snowball.getItem();
    if (!PDCUtils.isFlashBang(stack)) {
      return;
    }

    final Block block = event.getHitBlock();
    final Entity hitEntity = event.getHitEntity();
    final Location location;
    if (block == null) {
      if (hitEntity == null) {
        return;
      }
      location = hitEntity.getLocation();
    } else {
      location = block.getLocation();
    }

    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(
        Particle.DUST, location, 25, 0.5, 0.5, 0.5, 0.5, new DustOptions(Color.YELLOW, 4));

    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllMurderers(killer -> {
      final Location killerLocation = killer.getLocation();
      final double distance = killerLocation.distanceSquared(location);
      if (distance < 4) {
        killer.addPotionEffects(
            PotionEffectType.BLINDNESS.createEffect(3 * 20, Integer.MAX_VALUE),
            PotionEffectType.SLOWNESS.createEffect(3 * 20, 4));
      }
    });
  }
}
