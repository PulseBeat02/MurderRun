package io.github.pulsebeat02.murderrun.gadget;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class ShockwaveTrap extends SurvivorTrap {

  public ShockwaveTrap() {
    super(
        "shockwave_trap",
        Material.TNT,
        Locale.SHOCKWAVE_TRAP_NAME.build(),
        Locale.SHOCKWAVE_TRAP_LORE.build(),
        Locale.SHOCKWAVE_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer survivor) {
    super.onTrapActivate(game, survivor);
    final Player player = survivor.getPlayer();
    final Location location = player.getLocation();
    location.getWorld().createExplosion(location, 0, false, false);
    final double radius = 10.0;
    location.getWorld().getPlayers().stream()
        .filter(p -> p.getLocation().distance(location) <= radius)
        .forEach(p -> {
          final Vector direction =
              p.getLocation().toVector().subtract(location.toVector()).normalize();
          p.setVelocity(direction.multiply(2));
        });
  }
}
