package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class FriendWarp extends SurvivorGadget {

  private static final String FRIEND_WARP_SOUND = "entity.enderman.teleport";

  public FriendWarp() {
    super(
        "friend_warp",
        Material.EMERALD,
        Message.FRIEND_WARP_NAME.build(),
        Message.FRIEND_WARP_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final GamePlayer target = this.getRandomSurvivorNotSame(manager, gamePlayer);
    final Location location = target.getLocation();
    gamePlayer.teleport(location);

    final PlayerAudience audience = gamePlayer.getAudience();
    audience.playSound(FRIEND_WARP_SOUND);

    final PlayerAudience targetAudience = target.getAudience();
    targetAudience.playSound(FRIEND_WARP_SOUND);
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
