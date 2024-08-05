package io.github.pulsebeat02.murderrun.game.gadget.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
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
        Locale.BURROW_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {

    super.onTrapActivate(game, murderer);

    final Location location = murderer.getLocation();
    final Location clone = location.clone();
    clone.subtract(0, 50, 0);

    murderer.apply(player -> {
      final MurderGameScheduler scheduler = game.getScheduler();
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
