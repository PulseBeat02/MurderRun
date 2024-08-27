package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class BurrowTrap extends SurvivorTrap {

  private static final int BURROW_TRAP_DURATION = 7 * 20;
  private static final String BURROW_TRAP_SOUND = "block.rooted_dirt.place";

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
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final Location location = murderer.getLocation();
    final Location clone = location.clone();
    clone.subtract(0, 50, 0);

    final GameScheduler scheduler = game.getScheduler();
    if (!(murderer instanceof final Killer killer)) {
      return;
    }

    killer.disableJump(scheduler, BURROW_TRAP_DURATION);
    killer.disableWalkNoFOVEffects(scheduler, BURROW_TRAP_DURATION);
    killer.setForceMineBlocks(false);
    killer.teleport(clone);
    killer.setGravity(true);
    scheduler.scheduleTask(() -> this.resetState(killer, location), BURROW_TRAP_DURATION);

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(BURROW_TRAP_SOUND);
  }

  private void resetState(final Killer killer, final Location location) {
    killer.teleport(location);
    killer.setForceMineBlocks(true);
  }
}
