package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class SpawnTrap extends SurvivorTrap {

  public SpawnTrap() {
    super(
        "spawn",
        Material.AMETHYST_SHARD,
        Message.SPAWN_NAME.build(),
        Message.SPAWN_LORE.build(),
        Message.SPAWN_ACTIVATE.build(),
        32,
        Color.GRAY);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location spawn = arena.getSpawn();
    murderer.teleport(spawn);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GadgetConstants.SPAWN_SOUND);
  }
}
