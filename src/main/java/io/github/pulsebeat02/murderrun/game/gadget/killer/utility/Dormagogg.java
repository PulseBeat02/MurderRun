package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Dormagogg extends KillerGadget {

  public Dormagogg() {
    super(
        "dormagogg",
        Material.WITHER_SKELETON_SKULL,
        Message.DORMAGOGG_NAME.build(),
        Message.DORMAGOGG_LORE.build(),
        16);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GamePlayer nearest = manager.getNearestSurvivor(location);
    final GamePlayer killer = manager.getGamePlayer(player);
    if (nearest == null) {
      return;
    }

    final Zombie dormagogg = this.spawnDormagogg(world, location, nearest);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.checkInteraction(scheduler, dormagogg, killer, nearest), 20L);
  }

  private void checkInteraction(
      final GameScheduler scheduler,
      final Zombie zombie,
      final GamePlayer killer,
      final GamePlayer nearest) {
    final Location origin = zombie.getLocation();
    final Location target = nearest.getLocation();
    final double distance = origin.distanceSquared(target);
    if (distance < 1) {
      this.applyDebuffs(scheduler, killer, nearest);
      zombie.remove();
    }
  }

  private void applyDebuffs(
      final GameScheduler scheduler, final GamePlayer killer, final GamePlayer survivor) {
    survivor.disableJump(scheduler, 7 * 20L);
    survivor.disableWalkWithFOVEffects(10 * 20);
    survivor.addPotionEffects(new PotionEffect(PotionEffectType.BLINDNESS, 7 * 20, 1));
    killer.setEntityGlowingForPlayer(survivor);
    scheduler.scheduleTask(() -> killer.removeEntityGlowingForPlayer(survivor), 7 * 20L);
  }

  private Zombie spawnDormagogg(
      final World world, final Location location, final GamePlayer nearest) {
    return world.spawn(location, Zombie.class, zombie -> {
      this.setTarget(zombie, nearest);
      if (zombie instanceof final Ageable ageable) {
        ageable.setBaby();
      }
    });
  }

  private void setTarget(final Zombie zombie, final GamePlayer nearest) {
    nearest.apply(zombie::setTarget);
  }
}
