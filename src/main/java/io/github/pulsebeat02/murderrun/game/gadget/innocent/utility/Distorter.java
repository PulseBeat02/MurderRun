package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
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
    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> manager.applyToAllMurderers(
            killer -> this.applyDistortionEffect(manager, killer, location)),
        0L,
        20L);
  }

  private void applyDistortionEffect(
      final MurderPlayerManager manager, final GamePlayer killer, final Location origin) {
    final Location location = killer.getLocation();
    final double distance = location.distanceSquared(origin);
    if (distance <= 1) {
      final Component message = Locale.DISTORTER_TRAP_DEACTIVATE.build();
      manager.applyToAllInnocents(innocent -> innocent.sendMessage(message));
    } else if (distance <= 100) {
      killer.spawnParticle(Particle.ELDER_GUARDIAN, location, 1, 0, 0, 0);
    }
  }
}
