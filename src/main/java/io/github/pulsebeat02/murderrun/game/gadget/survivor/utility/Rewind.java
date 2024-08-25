package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MovementManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Rewind extends SurvivorGadget {

  // global cooldown for each player
  private final Map<GamePlayer, Long> rewindCooldown;

  public Rewind() {
    super("rewind", Material.DIAMOND, Message.REWIND_NAME.build(), Message.REWIND_LORE.build(), 16);
    this.rewindCooldown = new HashMap<>();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final MovementManager movementManager = manager.getMovementManager();
    final GamePlayer survivor = manager.getGamePlayer(player);

    final long current = System.currentTimeMillis();
    if (!this.rewindCooldown.containsKey(survivor)) {
      this.rewindCooldown.put(survivor, current);
      this.handleRewind(game, event, movementManager, survivor, player, current);
      return;
    }

    final long value = this.rewindCooldown.get(survivor);
    if (current - value < 3000) {
      super.onGadgetDrop(game, event, false);
      return;
    }

    this.handleRewind(game, event, movementManager, survivor, player, current);
  }

  private void handleRewind(
      final Game game,
      final PlayerDropItemEvent event,
      final MovementManager movementManager,
      final GamePlayer survivor,
      final Player player,
      final long current) {
    final boolean successful = movementManager.handleRewind(survivor);
    player.setFallDistance(0.0f);
    this.rewindCooldown.put(survivor, current);
    super.onGadgetDrop(game, event, successful);
    survivor.playSound("entity.shulker.teleport");
  }
}
