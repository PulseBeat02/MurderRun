package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class GlowTrap extends SurvivorTrap {

  public GlowTrap() {
    super(
        "glow",
        Material.GLOWSTONE,
        Message.GLOW_NAME.build(),
        Message.GLOW_LORE.build(),
        Message.GLOW_ACTIVATE.build(),
        GameProperties.GLOW_COST,
        Color.YELLOW);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final PlayerManager manager = game.getPlayerManager();
    manager.setEntityGlowingForAliveInnocents(
        murderer, ChatColor.RED, GameProperties.GLOW_DURATION);
    manager.playSoundForAllParticipants(GameProperties.GLOW_SOUND);
  }
}
