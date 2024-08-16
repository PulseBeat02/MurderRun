package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Tracker extends SurvivorGadget {

  public Tracker() {
    super(
        "tracker",
        Material.TRIPWIRE_HOOK,
        Locale.TRACKER_TRAP_NAME.build(),
        Locale.TRACKER_TRAP_LORE.build(),
        64);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    manager.applyToAllMurderers(killer -> this.handleGlowing(killer, gamePlayer));
  }

  private void handleGlowing(final GamePlayer killer, final GamePlayer player) {
    final Location origin = player.getLocation();
    final Location killerLocation = killer.getLocation();
    final double distance = origin.distanceSquared(killerLocation);
    if (distance <= 25) {
      player.setEntityGlowingForPlayer(killer);
      player.sendMessage(Locale.TRACKER_TRAP_ACTIVATE.build());
    } else {
      player.sendMessage(Locale.TRACKER_TRAP_DEACTIVATE.build());
    }
  }
}
