package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;

public final class SpawnTrap extends SurvivorTrap {

  public SpawnTrap() {
    super(
        "spawn",
        Material.AMETHYST_SHARD,
        Locale.SPAWN_TRAP_NAME.build(),
        Locale.SPAWN_TRAP_LORE.build(),
        Locale.SPAWN_TRAP_ACTIVATE.build(),
        Color.GRAY);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final GameSettings settings = game.getSettings();
    final Arena arena = settings.getArena();
    final Location spawn = arena.getSpawn();
    murderer.teleport(spawn);
  }
}
