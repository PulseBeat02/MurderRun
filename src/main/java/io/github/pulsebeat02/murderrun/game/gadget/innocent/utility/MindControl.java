package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Murderer;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.Vector;

public final class MindControl extends MurderGadget {

  public MindControl() {
    super(
        "mind_control",
        Material.STRUCTURE_VOID,
        Locale.MIND_CONTROL_TRAP_NAME.build(),
        Locale.MIND_CONTROL_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, remove);

    final Player player = event.getPlayer();
    final MurderPlayerManager manager = game.getPlayerManager();
    final GamePlayer nearest = this.getNearestKiller(manager, player.getLocation());

    final Location origin = player.getLocation();
    final Location location = nearest.getLocation();
    player.teleport(location);

    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> this.applyMindControlEffects(player, nearest), 0L, 1L, 10 * 20);
    scheduler.scheduleTask(() -> player.teleport(origin), 20 * 20L);
  }

  private void applyMindControlEffects(final Player player, final GamePlayer killer) {
    final Location location = player.getLocation();
    final Vector velocity = player.getVelocity();
    killer.apply(other -> {
      other.teleport(location);
      other.setVelocity(velocity);
    });
  }

  private GamePlayer getNearestKiller(final MurderPlayerManager manager, final Location origin) {
    GamePlayer nearest = null;
    double min = Double.MAX_VALUE;
    final Collection<Murderer> killers = manager.getMurderers();
    for (final GamePlayer killer : killers) {
      final Location location = killer.getLocation();
      final double distance = location.distanceSquared(origin);
      if (distance < min) {
        nearest = killer;
        min = distance;
      }
    }
    return nearest;
  }
}
