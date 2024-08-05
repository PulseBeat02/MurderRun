package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.Innocent;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class FriendWarp extends MurderGadget {

  public FriendWarp() {
    super(
        "friend_warp",
        Material.EMERALD,
        Locale.FRIEND_WARP_TRAP_NAME.build(),
        Locale.FRIEND_WARP_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onDropEvent(game, event, true);

    final Player player = event.getPlayer();
    final MurderPlayerManager manager = game.getPlayerManager();
    final Collection<Innocent> innocents = manager.getInnocentPlayers();
    final List<Innocent> list = new ArrayList<>(innocents);
    if (list.isEmpty()) {
      return;
    }

    Collections.shuffle(list);
    final Innocent target = list.getFirst();
    final Location location = target.getLocation();
    player.teleport(location);
  }
}
