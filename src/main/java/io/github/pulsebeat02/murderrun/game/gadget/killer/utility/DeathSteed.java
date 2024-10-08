package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.HorseInventory;

public final class DeathSteed extends KillerGadget {

  public DeathSteed() {
    super(
      "death_steed",
      Material.SADDLE,
      Message.DEATH_STEED_NAME.build(),
      Message.DEATH_STEED_LORE.build(),
      GameProperties.DEATH_STEED_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final Game game, final GamePlayer player, final Item item, final boolean remove) {
    super.onGadgetDrop(game, player, item, true);

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final Horse horse = this.spawnHorse(world, location, player);
    final GameScheduler scheduler = game.getScheduler();
    final PlayerManager manager = game.getPlayerManager();
    scheduler.scheduleConditionalTask(() -> this.handleSurvivors(manager, horse), 0, 5L, horse::isDead);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.DEATH_STEED_SOUND);

    return false;
  }

  private void handleSurvivors(final PlayerManager manager, final Horse horse) {
    manager.applyToAllLivingInnocents(survivor -> this.handleSurvivor(survivor, horse));
  }

  private Horse spawnHorse(final World world, final Location location, final GamePlayer player) {
    return world.spawn(location, Horse.class, entity -> {
      final Player owner = player.getInternalPlayer();
      this.customizeAttributes(entity, owner);
      this.setSaddle(entity);
    });
  }

  private void customizeAttributes(final Horse entity, final Player owner) {
    entity.setTamed(true);
    entity.setOwner(owner);
    entity.addPassenger(owner);
    entity.setColor(Color.BLACK);
  }

  private void setSaddle(final Horse horse) {
    final HorseInventory inventory = horse.getInventory();
    inventory.setSaddle(ItemFactory.createSaddle());
  }

  private void handleSurvivor(final GamePlayer survivor, final Horse horse) {
    final Location survivorLocation = survivor.getLocation();
    final Location updated = survivorLocation.add(0, 2, 0);
    final Location horseLocation = horse.getLocation();
    final double distance = updated.distanceSquared(horseLocation);
    if (distance < 400) {
      this.spawnParticleLine(updated, horseLocation);
    }
  }

  private void spawnParticleLine(final Location start, final Location end) {
    final World world = requireNonNull(start.getWorld());
    final double distance = start.distance(end) - 3;
    final double step = 0.1;
    for (double d = 0; d < distance; d += step) {
      final double t = d / distance;
      final double x = start.getX() + (end.getX() - start.getX()) * t;
      final double y = start.getY() + (end.getY() - start.getY()) * t;
      final double z = start.getZ() + (end.getZ() - start.getZ()) * t;
      world.spawnParticle(Particle.BUBBLE, x, y, z, 5);
    }
  }
}
