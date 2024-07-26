package io.github.pulsebeat02.murderrun.utils;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public final class PlayerUtils {

  private static final Map<Player, Team> GLOW_TEAMS = new WeakHashMap<>();

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

  public static void setGlowColor(final GamePlayer gamePlayer, final ChatColor color) {
    final Player player = gamePlayer.getPlayer();
    final ScoreboardManager manager = Bukkit.getScoreboardManager();
    if (manager == null) {
      throw new AssertionError("Unable to access main scoreboard!");
    }
    final Scoreboard scoreboard = manager.getMainScoreboard();
    final UUID glowID = UUID.randomUUID();
    final String name = String.format("color-%s", glowID);
    final Team team = scoreboard.registerNewTeam(name);
    team.setColor(color);
    team.addEntry(player.getDisplayName());
    GLOW_TEAMS.put(player, team);
    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0));
  }

  public static void removeGlow(final GamePlayer gamePlayer) {
    final Player player = gamePlayer.getPlayer();
    player.removePotionEffect(PotionEffectType.GLOWING);
    final Team team = GLOW_TEAMS.get(player);
    if (team == null) {
      return;
    }
    team.unregister();
    GLOW_TEAMS.remove(player);
  }
}
