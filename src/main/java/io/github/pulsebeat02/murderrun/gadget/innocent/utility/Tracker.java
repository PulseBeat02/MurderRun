package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.reflect.NMSHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Tracker extends MurderGadget {

  public Tracker() {
    super(
        "tracker",
        Material.TRIPWIRE_HOOK,
        Locale.TRACKER_TRAP_NAME.build(),
        Locale.TRACKER_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    super.onDropEvent(game, event);
    final Player player = event.getPlayer();
    final Location origin = player.getLocation();
    final MurderPlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    manager.applyToAllMurderers(killer -> {
      final Location killerLocation = killer.getLocation();
      if (origin.distanceSquared(killerLocation) <= 25) {
        final Player raw = killer.getPlayer();
        NMSHandler.getNmsUtils().sendGlowPacket(player, raw);
        gamePlayer.sendMessage(Locale.TRACKER_TRAP_ACTIVATE.build());
      } else {
        gamePlayer.sendMessage(Locale.TRACKER_TRAP_DEACTIVATE.build());
      }
    });
  }
}
