package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

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
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer[] players = this.getRandomPlayers(manager);
    final GamePlayer random = players[0];
    final GamePlayer random2 = players[1];

    final Location first = random.getLocation();
    final Location second = random2.getLocation();
    random.teleport(second);
    random2.teleport(first);

    final String sound = GadgetConstants.WARP_DISTORT_SOUND;
    final PlayerAudience randomAudience = random.getAudience();
    final PlayerAudience random2Audience = random2.getAudience();
    randomAudience.playSound(sound);
    random2Audience.playSound(sound);

    final Component msg = Message.WARP_DISTORT_ACTIVATE.build();
    randomAudience.sendMessage(msg);
    random2Audience.sendMessage(msg);

    return false;
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
