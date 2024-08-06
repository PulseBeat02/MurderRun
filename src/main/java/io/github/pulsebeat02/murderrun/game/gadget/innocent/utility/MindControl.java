package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
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

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final MurderPlayerManager manager = game.getPlayerManager();
    final GamePlayer nearest = manager.getNearestKiller(player.getLocation());

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
}
