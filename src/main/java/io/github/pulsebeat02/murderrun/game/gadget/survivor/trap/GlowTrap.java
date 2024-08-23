package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import java.util.Collection;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public final class GlowTrap extends SurvivorTrap {

  public GlowTrap() {
    super(
        "glow",
        Material.GLOWSTONE,
        Message.GLOW_NAME.build(),
        Message.GLOW_LORE.build(),
        Message.GLOW_ACTIVATE.build(),
        32,
        Color.YELLOW);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    final Collection<Survivor> players = manager.getInnocentPlayers();
    murderer.setGlowColorDuration(scheduler, 7 * 20L, ChatColor.RED, players);
    murderer.playSound(key("block.amethyst_block.chime"));
  }
}
