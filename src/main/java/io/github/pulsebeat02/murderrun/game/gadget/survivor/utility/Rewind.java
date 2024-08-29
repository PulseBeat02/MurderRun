package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MovementManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Rewind extends SurvivorGadget {

  private static final int REWIND_COOLDOWN = 3000;
  private static final String REWIND_SOUND = "entity.shulker.teleport";

  public Rewind() {
    super("rewind", Material.DIAMOND, Message.REWIND_NAME.build(), Message.REWIND_LORE.build(), 16);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    final PlayerManager manager = game.getPlayerManager();
    final MovementManager movementManager = manager.getMovementManager();
    if (!(player instanceof final Survivor survivor)) {
      return true;
    }

    final long current = System.currentTimeMillis();
    final long last = survivor.getRewindCooldown();
    if (last == 0) {
      survivor.setRewindCooldown(current);
      this.handleRewind(game, movementManager, survivor, item, current);
      return false;
    }

    if (current - last < REWIND_COOLDOWN) {
      return super.onGadgetDrop(game, player, item, false);
    }

    this.handleRewind(game, movementManager, survivor, item, current);

    return false;
  }

  private void handleRewind(
      final Game game,
      final MovementManager movementManager,
      final Survivor survivor,
      final Item item,
      final long current) {

    survivor.setRewindCooldown(current);
    survivor.setFallDistance(0.0f);

    final boolean successful = movementManager.handleRewind(survivor);
    super.onGadgetDrop(game, survivor, item, successful);

    final PlayerAudience audience = survivor.getAudience();
    audience.playSound(REWIND_SOUND);
  }
}
