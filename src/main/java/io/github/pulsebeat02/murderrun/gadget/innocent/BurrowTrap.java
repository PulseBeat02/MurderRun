package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.scheduler.MurderGameScheduler;
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
    final Player player = murderer.getPlayer();
    final Location location = murderer.getLocation();
    final Location clone = location.clone();
    clone.subtract(0, 50, 0);
    player.setGravity(false);
    player.teleport(clone);
    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.setBackDefault(player, location), 20 * 7);
  }

  private void setBackDefault(final Player player, final Location original) {
    player.teleport(original);
    player.setGravity(true);
  }
}
