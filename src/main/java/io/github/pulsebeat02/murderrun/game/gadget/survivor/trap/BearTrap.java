package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class BearTrap extends SurvivorTrap {

  private static final int BEAR_TRAP_DURATION = 5 * 20;
  private static final String BEAR_TRAP_SOUND = "block.anvil.destroy";

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
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    murderer.disableJump(scheduler, BEAR_TRAP_DURATION);
    murderer.disableWalkWithFOVEffects(BEAR_TRAP_DURATION);
    manager.playSoundForAllParticipants(BEAR_TRAP_SOUND);
  }
}
