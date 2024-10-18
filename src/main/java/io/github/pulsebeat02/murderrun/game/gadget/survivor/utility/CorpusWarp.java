package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class CorpusWarp extends SurvivorGadget {

  public CorpusWarp() {
    super(
      "corpus_warp",
      Material.PURPLE_STAINED_GLASS,
      Message.CORPUS_WARP_NAME.build(),
      Message.CORPUS_WARP_LORE.build(),
      GameProperties.CORPUS_WARP_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer target = manager.getRandomDeadPlayer();
    if (target == null) {
      return true;
    }

    final Location location = target.getDeathLocation();
    if (location == null) {
      return true;
    }
    player.teleport(location);
    item.remove();

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.CORPUS_WARP_SOUND);

    return false;
  }
}
