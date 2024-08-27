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

public final class CorpusWarp extends SurvivorGadget {

  private static final String CORPUS_WARP_SOUND = "entity.enderman.teleport";

  public CorpusWarp() {
    super(
        "corpus_warp",
        Material.PURPLE_STAINED_GLASS,
        Message.CORPUS_WARP_NAME.build(),
        Message.CORPUS_WARP_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer target = manager.getRandomDeadPlayer();
    if (target == null) {
      super.onGadgetDrop(game, event, false);
      return;
    }

    final Location location = target.getDeathLocation();
    if (location == null) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    gamePlayer.teleport(location);

    final PlayerAudience audience = gamePlayer.getAudience();
    audience.playSound(CORPUS_WARP_SOUND);
  }
}
