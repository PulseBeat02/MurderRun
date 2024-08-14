package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

public final class DeathSteed extends KillerGadget {

  public DeathSteed() {
    super(
        "death_steed",
        Material.SADDLE,
        Locale.DEATH_STEED_TRAP_NAME.build(),
        Locale.DEATH_STEED_TRAP_LORE.build(),
        64);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());

    final Horse horse = world.spawn(location, Horse.class, entity -> {
      entity.setOwner(player);
      entity.setCustomName("Death Steed");
      entity.setCustomNameVisible(true);
      entity.setTamed(true);

      final HorseInventory inventory = entity.getInventory();
      inventory.setSaddle(new ItemStack(Material.SADDLE));

      entity.addPassenger(player);
    });

    final GameScheduler scheduler = game.getScheduler();
    final PlayerManager manager = game.getPlayerManager();
    scheduler.scheduleTaskUntilCondition(
        () -> manager.applyToAllInnocents(survivor -> this.handleSurvivor(survivor, horse)),
        0,
        20,
        horse::isDead);
  }

  private void handleSurvivor(final Survivor survivor, final Horse horse) {
    final Location survivorLocation = survivor.getLocation();
    final Location horseLocation = horse.getLocation();
    final double distance = survivorLocation.distanceSquared(horseLocation);
    if (distance < 100) {
      this.spawnParticleLine(survivorLocation, horseLocation);
    }
  }

  private void spawnParticleLine(final Location start, final Location end) {
    final World world = requireNonNull(start.getWorld());
    final double distance = start.distance(end);
    final double step = 0.5;
    for (double d = 0; d < distance; d += step) {
      final double t = d / distance;
      final double x = start.getX() + (end.getX() - start.getX()) * t;
      final double y = start.getY() + (end.getY() - start.getY()) * t;
      final double z = start.getZ() + (end.getZ() - start.getZ()) * t;
      world.spawnParticle(Particle.BUBBLE_POP, x, y, z, 1);
    }
  }
}
