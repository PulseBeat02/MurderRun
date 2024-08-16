package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class BurrowTrap extends SurvivorTrap {

  public BurrowTrap() {
    super(
        "burrow",
        Material.DIRT,
        Locale.BURROW_TRAP_NAME.build(),
        Locale.BURROW_TRAP_LORE.build(),
        Locale.BURROW_TRAP_ACTIVATE.build(),
        32,
        new Color(49, 42, 41));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {

    final Location location = murderer.getLocation();
    final Location clone = location.clone();
    clone.subtract(0, 50, 0);

    murderer.apply(player -> {
      final GameScheduler scheduler = game.getScheduler();
      this.burrow(player, location);
      scheduler.scheduleTask(() -> this.setBackDefault(player, location), 20 * 7);
    });
  }

  private void burrow(final Player player, final Location original) {
    player.teleport(original);
    player.setGravity(false);
  }

  private void setBackDefault(final Player player, final Location original) {
    player.teleport(original);
    player.setGravity(true);
  }
}
