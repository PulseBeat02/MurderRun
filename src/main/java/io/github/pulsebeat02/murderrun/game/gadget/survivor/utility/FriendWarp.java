package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
        Locale.FRIEND_WARP_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final Collection<Survivor> survivors = manager.getInnocentPlayers();
    final List<Survivor> list = new ArrayList<>(survivors);
    if (list.isEmpty()) {
      return;
    }

    Collections.shuffle(list);
    final Survivor target = list.getFirst();
    final Location location = target.getLocation();
    player.teleport(location);
  }
}
