package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MovementManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Rewind extends SurvivorGadget {

  public Rewind() {
    super("rewind", Material.DIAMOND, Message.REWIND_NAME.build(), Message.REWIND_LORE.build(), 16);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final MovementManager movementManager = manager.getMovementManager();
    final GamePlayer survivor = manager.getGamePlayer(player);
    final boolean successful = movementManager.handleRewind(survivor);
    super.onGadgetDrop(game, event, successful);
  }
}
