package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public final class BlastOff extends SurvivorGadget implements Listener {

  private final Set<GamePlayer> restrictDismount;
  private final Game game;

  public BlastOff(final Game game) {
    super(
        "blast_off",
        Material.FIREWORK_ROCKET,
        Message.BLAST_OFF_NAME.build(),
        Message.BLAST_OFF_LORE.build(),
        32);
    this.game = game;
    this.restrictDismount = new HashSet<>();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer killer = manager.getNearestKiller(location);
    if (killer == null) {
      return;
    }

    final Location before = killer.getLocation();
    this.restrictDismount.add(killer);
    killer.apply(murderer -> {
      final Firework firework = this.spawnRocket(murderer);
      final GameScheduler scheduler = game.getScheduler();
      this.scheduleTeleportTask(scheduler, firework, killer, before);
    });

    final GamePlayer owner = manager.getGamePlayer(player);
    final PlayerAudience audience = owner.getAudience();
    audience.playSound("entity.firework_rocket.blast");
  }

  private void scheduleTeleportTask(
      final GameScheduler scheduler,
      final Firework firework,
      final GamePlayer killer,
      final Location before) {
    final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    scheduler.scheduleConditionalTask(
        () -> this.checkRideStatus(firework, killer, before, atomicBoolean),
        2 * 20L,
        2,
        atomicBoolean::get);
  }

  private void checkRideStatus(
      final Firework firework,
      final GamePlayer killer,
      final Location before,
      final AtomicBoolean atomicBoolean) {
    if (firework.isDead()) {
      killer.teleport(before);
      killer.apply(player -> player.setFallDistance(0.0f));
      this.restrictDismount.remove(killer);
      atomicBoolean.set(true);
    }
  }

  private Firework spawnRocket(final Player player) {
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    return world.spawn(location, Firework.class, firework -> {
      final FireworkMeta meta = firework.getFireworkMeta();
      meta.setPower(4);
      firework.setShotAtAngle(false);
      firework.setFireworkMeta(meta);
      firework.addPassenger(player);
    });
  }

  @EventHandler
  public void onEntityDismount(final EntityDismountEvent event) {

    final Entity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    final PlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (this.restrictDismount.contains(gamePlayer)) {
      event.setCancelled(true);
    }
  }
}
