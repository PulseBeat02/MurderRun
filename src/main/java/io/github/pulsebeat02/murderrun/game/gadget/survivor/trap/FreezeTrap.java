package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;

public final class FreezeTrap extends SurvivorTrap {

  public FreezeTrap() {
    super(
        "freeze",
        Material.PACKED_ICE,
        Message.FREEZE_NAME.build(),
        Message.FREEZE_LORE.build(),
        Message.FREEZE_ACTIVATE.build(),
        32,
        Color.BLUE);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {
    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    murderer.disableJump(scheduler, 7 * 20L);
    murderer.disableWalkWithFOVEffects(10 * 20);
    murderer.apply(player -> player.setFreezeTicks(7 * 20));
    manager.playSoundForAllParticipants("entity.player.hurt_freeze");
  }
}
