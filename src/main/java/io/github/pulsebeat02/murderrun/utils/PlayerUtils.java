package io.github.pulsebeat02.murderrun.utils;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
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

  public static void removeAllPotionEffects(final Player player) {
    player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
  }
}
