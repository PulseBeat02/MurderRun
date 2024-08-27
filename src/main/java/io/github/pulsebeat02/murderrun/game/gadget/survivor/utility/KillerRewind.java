package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MovementManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class KillerRewind extends SurvivorGadget {

  // global cooldown for each player
  private final Map<GamePlayer, Long> rewindCooldown;

  public KillerRewind() {
    super(
        "killer_rewind",
        Material.LAPIS_BLOCK,
        Message.MURDERER_REWIND_NAME.build(),
        Message.MURDERER_REWIND_LORE.build(),
        16);
    this.rewindCooldown = new HashMap<>();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final MovementManager movementManager = manager.getMovementManager();
    final GamePlayer closest = manager.getNearestKiller(location);
    if (closest == null) {
      return;
    }

    final long current = System.currentTimeMillis();
    if (!this.rewindCooldown.containsKey(closest)) {
      this.rewindCooldown.put(closest, current);
      this.handleRewind(game, event, movementManager, closest, player, current);
      return;
    }

    final long value = this.rewindCooldown.get(closest);
    if (current - value < 3000) {
      super.onGadgetDrop(game, event, false);
      return;
    }

    this.handleRewind(game, event, movementManager, closest, player, current);
  }

  private void handleRewind(
      final Game game,
      final PlayerDropItemEvent event,
      final MovementManager movementManager,
      final GamePlayer killer,
      final Player player,
      final long current) {
    final boolean successful = movementManager.handleRewind(killer);
    player.setFallDistance(0.0f);
    this.rewindCooldown.put(killer, current);
    super.onGadgetDrop(game, event, successful);
    final PlayerAudience audience = killer.getAudience();
    audience.playSound("entity.shulker.teleport");
  }
}
