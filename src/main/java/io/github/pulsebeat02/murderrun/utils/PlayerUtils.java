package io.github.pulsebeat02.murderrun.utils;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public final class PlayerUtils {

  private PlayerUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Optional<GamePlayer> checkIfValidPlayer(
      final MurderGame game, final Player player) {
    final UUID uuid = player.getUniqueId();
    final PlayerManager manager = game.getPlayerManager();
    return manager.lookupPlayer(uuid);
  }
}
