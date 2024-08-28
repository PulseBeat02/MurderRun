package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class SixthSense extends SurvivorGadget {

  private static final double SIXTH_SENSE_RADIUS = 10D;
  private static final String SIXTH_SENSE_SOUND = "entity.sniffer.digging";

  public SixthSense() {
    super(
        "sixth_sense",
        Material.GOLDEN_CARROT,
        Message.SIXTH_SENSE_NAME.build(),
        Message.SIXTH_SENSE_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (!(gamePlayer instanceof final Survivor survivor)) {
      return;
    }

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleKillers(manager, survivor), 0, 2 * 20L);

    final PlayerAudience audience = gamePlayer.getAudience();
    final Component message = Message.SIXTH_SENSE_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(SIXTH_SENSE_SOUND);
  }

  private void handleKillers(final PlayerManager manager, final Survivor player) {
    manager.applyToAllMurderers(murderer -> this.handleGlowMurderer(murderer, player));
  }

  private void handleGlowMurderer(final GamePlayer killer, final Survivor survivor) {
    final Location location = survivor.getLocation();
    final Location other = killer.getLocation();
    final Collection<GamePlayer> visible = survivor.getGlowingKillers();
    final double distance = location.distanceSquared(other);
    final MetadataManager metadata = survivor.getMetadataManager();
    if (distance < SIXTH_SENSE_RADIUS * SIXTH_SENSE_RADIUS) {
      visible.add(killer);
      metadata.setEntityGlowing(killer, ChatColor.BLUE, true);
    } else if (visible.contains(killer)) {
      visible.remove(killer);
      metadata.setEntityGlowing(killer, ChatColor.BLUE, false);
    }
  }
}
