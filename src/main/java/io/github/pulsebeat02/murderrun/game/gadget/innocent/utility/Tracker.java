package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
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
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onDropEvent(game, event, true);

    final Player player = event.getPlayer();
    final MurderPlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    manager.applyToAllMurderers(killer -> this.handleGlowing(killer, gamePlayer, player));
  }

  private void handleGlowing(
      final GamePlayer killer, final GamePlayer player, final Player playerRaw) {
    final Location origin = player.getLocation();
    final Location killerLocation = killer.getLocation();
    final double distance = origin.distanceSquared(killerLocation);
    if (distance <= 25) {
      killer.apply(raw -> {
        NMSHandler.getNmsUtils().sendGlowPacket(playerRaw, raw);
        player.sendMessage(Locale.TRACKER_TRAP_ACTIVATE.build());
      });
    } else {
      player.sendMessage(Locale.TRACKER_TRAP_DEACTIVATE.build());
    }
  }
}
