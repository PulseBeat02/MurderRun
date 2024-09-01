package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class FreezeTrap extends SurvivorTrap {

  public FreezeTrap() {
    super(
        "freeze",
        Material.PACKED_ICE,
        Message.FREEZE_NAME.build(),
        Message.FREEZE_LORE.build(),
        Message.FREEZE_ACTIVATE.build(),
        GameProperties.FREEZE_COST,
        Color.BLUE);
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final GameScheduler scheduler = game.getScheduler();
    final int duration = GameProperties.FREEZE_EFFECT_DURATION;
    murderer.disableJump(scheduler, duration);
    murderer.setFreezeTicks(duration);
    murderer.disableWalkWithFOVEffects(GameProperties.FREEZE_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.FREEZE_SOUND);
  }
}
