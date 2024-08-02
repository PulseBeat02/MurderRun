package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Distorter extends MurderGadget {

  public Distorter() {
    super(
        "distorter",
        Material.END_STONE,
        Locale.DISTORTER_TRAP_NAME.build(),
        Locale.DISTORTER_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {
    super.onDropEvent(game, event, false);
    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final MurderPlayerManager manager = game.getPlayerManager();
    game.getScheduler()
        .scheduleRepeatedTask(
            () -> {
              manager.applyToAllMurderers(killer -> {
                final Player murderer = killer.getPlayer();
                final Location killerLocation = murderer.getLocation();
                final double distance = killerLocation.distanceSquared(location);
                if (distance <= 1) {
                  manager.applyToAllInnocents(
                      innocent -> innocent.sendMessage(Locale.DISTORTER_TRAP_DEACTIVATE.build()));
                } else if (distance <= 100) {
                  murderer.spawnParticle(
                      Particle.ELDER_GUARDIAN, killer.getLocation(), 20, 1, 1, 1, 0.1);
                }
              });
            },
            0L,
            20L);
  }
}
