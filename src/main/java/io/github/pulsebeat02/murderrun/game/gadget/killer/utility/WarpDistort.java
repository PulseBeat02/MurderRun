package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class WarpDistort extends KillerGadget {

  public WarpDistort() {
    super(
        "warp_distort",
        Material.CHORUS_FRUIT,
        Message.WARP_DISTORT_NAME.build(),
        Message.WARP_DISTORT_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer[] players = this.getRandomPlayers(manager);
    final GamePlayer random = players[0];
    final GamePlayer random2 = players[1];

    final Location first = random.getLocation();
    final Location second = random2.getLocation();
    random.teleport(second);
    random2.teleport(first);
    random.playSound("entity.enderman.teleport");
    random2.playSound("entity.enderman.teleport");

    final Component msg = Message.WARP_DISTORT_ACTIVATE.build();
    random.sendMessage(msg);
    random2.sendMessage(msg);
  }

  private GamePlayer[] getRandomPlayers(final PlayerManager manager) {
    final GamePlayer random = manager.getRandomAliveInnocentPlayer();
    GamePlayer random2 = manager.getRandomAliveInnocentPlayer();
    while (random == random2) {
      random2 = manager.getRandomAliveInnocentPlayer();
    }
    return new GamePlayer[] {random, random2};
  }
}
