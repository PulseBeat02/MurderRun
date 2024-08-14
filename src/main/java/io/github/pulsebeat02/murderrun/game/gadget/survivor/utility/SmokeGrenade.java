package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.scheduler.UnattachedTemporaryRepeatedTask;
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

public final class SmokeGrenade extends SurvivorGadget implements Listener {

  public SmokeGrenade(final MurderRun plugin) {
    super(
        "smoke_grenade",
        Material.SNOWBALL,
        Locale.SMOKE_BOMB_TRAP_NAME.build(),
        Locale.SMOKE_BOMB_TRAP_LORE.build(),
        16,
        stack -> ItemUtils.setPersistentDataAttribute(
            stack, Keys.SMOKE_GRENADE, PersistentDataType.BOOLEAN, true));
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onProjectileHitEvent(final ProjectileHitEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Snowball snowball)) {
      return;
    }

    final ItemStack stack = snowball.getItem();
    if (!ItemUtils.isSmokeGrenade(stack)) {
      return;
    }

    final Block block = event.getHitBlock();
    if (block == null) {
      return;
    }

    final Location location = block.getLocation();
    final World world = requireNonNull(location.getWorld());
    final UnattachedTemporaryRepeatedTask task = new UnattachedTemporaryRepeatedTask(
        () -> world.spawnParticle(Particle.SMOKE, location, 50, 2, 2, 2), 10, 5 * 20);
    task.run();

    final List<Entity> entities = entity.getNearbyEntities(1, 1, 1);
    for (final Entity nearby : entities) {
      if (!(nearby instanceof final Player player)) {
        return;
      }
      player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(20, 0));
    }
  }
}
