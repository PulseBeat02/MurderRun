package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Tracker extends SurvivorGadget {

  public Tracker() {
    super(
      "tracker",
      Material.COMPASS,
      Message.TRACKER_NAME.build(),
      Message.TRACKER_LORE.build(),
      GameProperties.TRACKER_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final Game game, final GamePlayer player, final Item item, final boolean remove) {
    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllMurderers(killer -> this.handleGlowing(killer, player));

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.TRACKER_SOUND);

    return false;
  }

  private void handleGlowing(final GamePlayer killer, final GamePlayer player) {
    final Location origin = player.getLocation();
    final Location killerLocation = killer.getLocation();
    final double distance = origin.distanceSquared(killerLocation);
    final MetadataManager metadata = player.getMetadataManager();
    final PlayerAudience audience = player.getAudience();
    final double radius = GameProperties.TRACKER_RADIUS;
    if (distance < radius * radius) {
      metadata.setEntityGlowing(killer, ChatColor.DARK_PURPLE, true);
      audience.sendMessage(Message.TRACKER_ACTIVATE.build());
    } else {
      audience.sendMessage(Message.TRACKER_DEACTIVATE.build());
    }
  }
}
