package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

public final class FlashBang extends SurvivorGadget implements Listener {

  public FlashBang(final MurderRun plugin) {
    super(
        "flash_bang",
        Material.SNOWBALL,
        Locale.FLASHBANG_TRAP_NAME.build(),
        Locale.FLASHBANG_TRAP_LORE.build(),
        stack -> ItemUtils.setPersistentDataAttribute(
            stack, Keys.FLASH_BANG, PersistentDataType.BOOLEAN, true));
    Bukkit.getPluginManager().registerEvents(this, plugin);
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
    world.spawnParticle(Particle.WHITE_ASH, location, 30, 1, 1, 1, 1, 1);

    final List<Entity> entities = entity.getNearbyEntities(1, 1, 1);
    for (final Entity nearby : entities) {
      if (!(nearby instanceof final Player player)) {
        return;
      }
      player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(10, 0));
      player.addPotionEffect(PotionEffectType.SLOWNESS.createEffect(10, 1));
    }
  }
}
