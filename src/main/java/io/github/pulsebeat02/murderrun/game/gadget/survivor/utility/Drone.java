package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Drone extends Gadget {

  public Drone() {
    super(
        "drone",
        Material.ENDER_EYE,
        Locale.DRONE_TRAP_NAME.build(),
        Locale.DRONE_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location origin = player.getLocation();
    final Location drone = origin.add(0, 20, 0);
    player.setGameMode(GameMode.SPECTATOR);
    player.teleport(drone);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.setDefault(player, origin), 20 * 20L);
  }

  private void setDefault(final Player player, final Location origin) {
    player.teleport(origin);
    player.setGameMode(GameMode.ADVENTURE);
  }
}
