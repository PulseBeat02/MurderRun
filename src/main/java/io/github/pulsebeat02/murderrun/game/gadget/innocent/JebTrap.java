package io.github.pulsebeat02.murderrun.game.gadget.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Sheep;

public final class JebTrap extends SurvivorTrap {

  private static final int SHEEP_COUNT = 15;

  public JebTrap() {
    super(
        "jeb",
        Material.SHEEP_SPAWN_EGG,
        Locale.JEB_TRAP_NAME.build(),
        Locale.JEB_TRAP_LORE.build(),
        Locale.JEB_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    final Location location = murderer.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    for (int i = 0; i < SHEEP_COUNT; i++) {
      world.spawn(location, Sheep.class, sheep -> {
        sheep.setCustomName("jeb_");
      });
    }
  }
}
