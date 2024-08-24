package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MovementManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class KillerRewind extends SurvivorGadget {

  public KillerRewind() {
    super(
        "killer_rewind",
        Material.LAPIS_BLOCK,
        Message.MURDERER_REWIND_NAME.build(),
        Message.MURDERER_REWIND_LORE.build(),
        16);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer closest = manager.getNearestKiller(location);
    if (closest == null) {
      return;
    }

    final MovementManager movementManager = manager.getMovementManager();
    final boolean successful = movementManager.handleRewind(closest);
    closest.apply(raw -> raw.setFallDistance(0.0f));
    super.onGadgetDrop(game, event, successful);

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    gamePlayer.playSound("entity.shulker.teleport");
  }
}
