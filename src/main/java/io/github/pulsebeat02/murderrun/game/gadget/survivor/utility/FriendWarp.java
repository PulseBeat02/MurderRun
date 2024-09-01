package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class FriendWarp extends SurvivorGadget {

  public FriendWarp() {
    super(
        "friend_warp",
        Material.EMERALD,
        Message.FRIEND_WARP_NAME.build(),
        Message.FRIEND_WARP_LORE.build(),
        GameProperties.FRIEND_WARP_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer target = this.getRandomSurvivorNotSame(manager, player);
    final Location location = target.getLocation();
    player.teleport(location);

    final String sound = GameProperties.FRIEND_WARP_SOUND;
    final PlayerAudience audience = player.getAudience();
    audience.playSound(sound);

    final PlayerAudience targetAudience = target.getAudience();
    targetAudience.playSound(sound);

    return false;
  }

  private GamePlayer getRandomSurvivorNotSame(
      final PlayerManager manager, final GamePlayer gamePlayer) {
    GamePlayer random = manager.getRandomAliveInnocentPlayer();
    while (random == gamePlayer) {
      random = manager.getRandomAliveInnocentPlayer();
    }
    return random;
  }
}
