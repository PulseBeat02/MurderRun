package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
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
  public void onTrapActivate(final Game game, final GamePlayer murderer) {

    final Location location = murderer.getLocation();
    final Location clone = location.clone();
    clone.subtract(0, 50, 0);

    final GameScheduler scheduler = game.getScheduler();
    murderer.disableJump(scheduler, 7 * 20L);
    murderer.disableWalkNoFOVEffects(scheduler, 7 * 20L);

    if (murderer instanceof final Killer killer) {
      killer.setForceMineBlocks(false);
    }
    murderer.playSound(key("block.rooted_dirt.place"));

    murderer.apply(player -> {
      player.teleport(clone);
      player.setGravity(true);
      scheduler.scheduleTask(() -> this.resetPlayer(murderer, player, location), 7 * 20L);
    });
  }

  private void resetPlayer(
      final GamePlayer murderer, final Player player, final Location location) {
    player.teleport(location);
    if (murderer instanceof final Killer killer) {
      killer.setForceMineBlocks(true);
    }
  }
}
