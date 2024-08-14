package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class FriendWarp extends SurvivorGadget {

  public FriendWarp() {
    super(
        "friend_warp",
        Material.EMERALD,
        Locale.FRIEND_WARP_TRAP_NAME.build(),
        Locale.FRIEND_WARP_TRAP_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final Survivor target = manager.getRandomAliveInnocentPlayer();
    final Location location = target.getLocation();
    player.teleport(location);
  }
}
