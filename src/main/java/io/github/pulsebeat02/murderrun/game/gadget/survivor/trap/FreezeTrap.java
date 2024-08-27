package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class FreezeTrap extends SurvivorTrap {

  private static final int FREEZE_TRAP_DURATION = 7 * 20;
  private static final int FREEZE_TRAP_EFFECT_DURATION = 10 * 20;
  private static final String FREEZE_TRAP_SOUND = "block.glass.break";

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
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final GameScheduler scheduler = game.getScheduler();
    murderer.disableJump(scheduler, FREEZE_TRAP_DURATION);
    murderer.disableWalkWithFOVEffects(FREEZE_TRAP_EFFECT_DURATION);
    murderer.setFreezeTicks(FREEZE_TRAP_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(FREEZE_TRAP_SOUND);
  }
}
