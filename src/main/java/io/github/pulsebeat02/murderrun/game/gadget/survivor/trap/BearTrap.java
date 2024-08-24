package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;

public final class BearTrap extends SurvivorTrap {

  public BearTrap() {
    super(
        "bear",
        Material.IRON_TRAPDOOR,
        Message.BEAR_NAME.build(),
        Message.BEAR_LORE.build(),
        Message.BEAR_ACTIVATE.build(),
        16,
        new Color(35, 23, 9));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    murderer.disableJump(scheduler, 5 * 20L);
    murderer.disableWalkWithFOVEffects(5 * 20);
    manager.playSoundForAllParticipants("block.anvil.destroy");
  }
}
