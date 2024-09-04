package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class BearTrap extends SurvivorTrap {

  public BearTrap() {
    super(
        "bear",
        Material.IRON_TRAPDOOR,
        Message.BEAR_NAME.build(),
        Message.BEAR_LORE.build(),
        Message.BEAR_ACTIVATE.build(),
        GameProperties.BEAR_COST,
        new Color(35, 23, 9));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final GameScheduler scheduler = game.getScheduler();
    final int duration = GameProperties.BEAR_DURATION;
    murderer.disableJump(scheduler, duration);
    murderer.disableWalkWithFOVEffects(duration);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.BEAR_SOUND);
  }
}
