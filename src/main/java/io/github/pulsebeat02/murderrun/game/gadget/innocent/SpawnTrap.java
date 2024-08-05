package io.github.pulsebeat02.murderrun.game.gadget.innocent;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import io.github.pulsebeat02.murderrun.game.arena.MurderArena;
import io.github.pulsebeat02.murderrun.game.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;

public final class SpawnTrap extends SurvivorTrap {

  public SpawnTrap() {
    super(
        "spawn",
        Material.AMETHYST_SHARD,
        Locale.SPAWN_TRAP_NAME.build(),
        Locale.SPAWN_TRAP_LORE.build(),
        Locale.SPAWN_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    final MurderSettings settings = game.getSettings();
    final MurderArena arena = settings.getArena();
    final Location spawn = arena.getSpawn();
    murderer.teleport(spawn);
  }
}
