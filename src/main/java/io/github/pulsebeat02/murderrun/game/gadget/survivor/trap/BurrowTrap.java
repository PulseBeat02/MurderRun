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
import org.bukkit.entity.Player;

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
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {

    final Location location = murderer.getLocation();
    final Location clone = location.clone();
    clone.subtract(0, 50, 0);

    final GameScheduler scheduler = game.getScheduler();
    if (murderer instanceof final Killer killer) {
      murderer.disableJump(scheduler, 7 * 20L);
      murderer.disableWalkNoFOVEffects(scheduler, 7 * 20L);
      killer.setForceMineBlocks(false);
      killer.apply(player -> {
        player.teleport(clone);
        player.setGravity(true);
        scheduler.scheduleTask(() -> this.resetPlayer(killer, player, location), 7 * 20L);
      });
    }

    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants("block.rooted_dirt.place");
  }

  private void resetPlayer(
      final GamePlayer murderer, final Player player, final Location location) {
    player.teleport(location);
    if (murderer instanceof final Killer killer) {
      killer.setForceMineBlocks(true);
    }
  }
}
