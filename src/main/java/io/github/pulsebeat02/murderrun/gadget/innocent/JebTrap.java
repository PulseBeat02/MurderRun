package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;

public final class JebTrap extends SurvivorTrap {

  public JebTrap() {
    super(
        "jeb",
        Material.SHEEP_SPAWN_EGG,
        Locale.JEB_TRAP_NAME.build(),
        Locale.JEB_TRAP_LORE.build(),
        Locale.JEB_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer survivor) {
    super.onTrapActivate(game, survivor);
    final Player player = survivor.getPlayer();
    final Location location = player.getLocation();
    for (int i = 0; i < 15; i++) {
      final Sheep sheep = (Sheep) location.getWorld().spawnEntity(location, EntityType.SHEEP);
      sheep.setCustomName("jeb_");
    }
  }
}
