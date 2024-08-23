package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;

public final class BurrowTrap extends SurvivorTrap {

  public BurrowTrap() {
    super(
        "burrow",
        Material.DIRT,
        Message.BURROW_NAME.build(),
        Message.BURROW_LORE.build(),
        Message.BURROW_ACTIVATE.build(),
        32,
        new Color(49, 42, 41));
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer) {

    final Location location = murderer.getLocation();
    final Location clone = location.clone();
    clone.subtract(0, 50, 0);

    final GameScheduler scheduler = game.getScheduler();
    murderer.disableJump(scheduler, 7 * 20L);
    murderer.disableWalkNoFOVEffects(scheduler, 7 * 20L);
    murderer.setForceMineBlocks(false);

    murderer.apply(player -> {
      player.teleport(clone);
      player.setGravity(true);
      scheduler.scheduleTask(
          () -> {
            player.teleport(location);
            murderer.setForceMineBlocks(true);
          },
          7 * 20L);
    });
  }
}
