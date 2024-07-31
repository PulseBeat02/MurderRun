package io.github.pulsebeat02.murderrun.gadget.innocent;

import io.github.pulsebeat02.murderrun.gadget.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.InnocentPlayer;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
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
        Locale.GLOW_TRAP_ACTIVATE.build());
  }

  @Override
  public void onTrapActivate(final MurderGame game, final GamePlayer murderer) {
    super.onTrapActivate(game, murderer);
    final MurderPlayerManager manager = game.getPlayerManager();
    final Collection<InnocentPlayer> players = manager.getInnocentPlayers();
    final Collection<GamePlayer> higher =
        players.stream().map(player -> (GamePlayer) player).toList();
    final MurderGameScheduler scheduler = game.getScheduler();
    PlayerUtils.setGlowColor(murderer, ChatColor.RED, higher);
    scheduler.scheduleTask(() -> PlayerUtils.removeGlow(murderer, higher), 7 * 20);
  }
}
