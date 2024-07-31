package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.InnocentPlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
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
  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    super.onDropEvent(game, event);
    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final Collection<InnocentPlayer> innocents = manager.getInnocentPlayers();
    final List<InnocentPlayer> copy = new ArrayList<>(innocents);
    Collections.shuffle(copy);
    final InnocentPlayer target = copy.get(0);
    final Location location = target.getLocation();
    player.teleport(location);
  }
}
