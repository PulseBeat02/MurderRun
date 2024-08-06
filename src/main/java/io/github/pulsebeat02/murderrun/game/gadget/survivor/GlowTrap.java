package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import java.awt.Color;
import java.util.Collection;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public final class GlowTrap extends SurvivorTrap {

  public GlowTrap() {
    super(
        "glow",
        Material.GLOWSTONE,
        Locale.GLOW_TRAP_NAME.build(),
        Locale.GLOW_TRAP_LORE.build(),
        Locale.GLOW_TRAP_ACTIVATE.build(),
        Color.YELLOW);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final PlayerManager manager = game.getPlayerManager();
    final Collection<Survivor> players = manager.getInnocentPlayers();
    final Collection<GamePlayer> higher =
        players.stream().map(player -> (GamePlayer) player).toList();
    PlayerUtils.setGlowColor(murderer, ChatColor.RED, higher);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> PlayerUtils.removeGlow(murderer, higher), 7 * 20);
  }
}
