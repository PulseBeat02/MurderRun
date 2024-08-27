package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Tracker extends SurvivorGadget {

  public Tracker() {
    super(
        "tracker",
        Material.COMPASS,
        Message.TRACKER_NAME.build(),
        Message.TRACKER_LORE.build(),
        64);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    manager.applyToAllMurderers(killer -> this.handleGlowing(killer, gamePlayer));

    final PlayerAudience audience = gamePlayer.getAudience();
    audience.playSound("entity.experience_orb.pickup");
  }

  private void handleGlowing(final GamePlayer killer, final GamePlayer player) {
    final Location origin = player.getLocation();
    final Location killerLocation = killer.getLocation();
    final double distance = origin.distanceSquared(killerLocation);
    final MetadataManager metadata = player.getMetadataManager();
    final PlayerAudience audience = player.getAudience();
    if (distance < 25) {
      metadata.setEntityGlowing(killer, ChatColor.RED, true);
      audience.sendMessage(Message.TRACKER_ACTIVATE.build());
    } else {
      audience.sendMessage(Message.TRACKER_DEACTIVATE.build());
    }
  }
}
