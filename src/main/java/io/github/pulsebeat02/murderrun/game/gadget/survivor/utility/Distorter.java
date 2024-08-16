package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Distorter extends SurvivorGadget {

  public Distorter() {
    super(
        "distorter",
        Material.END_STONE,
        Locale.DISTORTER_TRAP_NAME.build(),
        Locale.DISTORTER_TRAP_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, false);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> handleAllKillers(manager, location), 0L, 20L);
  }

  private void handleAllKillers(final PlayerManager manager, final Location location) {
    manager.applyToAllMurderers(killer -> this.applyDistortionEffect(manager, killer, location));
  }

  private void applyDistortionEffect(
      final PlayerManager manager, final GamePlayer killer, final Location origin) {
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
