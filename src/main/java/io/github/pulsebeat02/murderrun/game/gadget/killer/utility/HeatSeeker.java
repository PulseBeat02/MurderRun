package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class HeatSeeker extends KillerGadget {

  private static final String HEAT_SEEKER_SOUND = "block.amethyst_block.chime";
  private static final double HEAT_SEEKER_RADIUS = 10D;

  public HeatSeeker() {
    super(
        "heat_seeker",
        Material.BLAZE_ROD,
        Message.HEAT_SEEKER_NAME.build(),
        Message.HEAT_SEEKER_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (!(gamePlayer instanceof final Killer killer)) {
      return;
    }

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.scheduleTasks(manager, killer), 0, 2 * 20L);

    final PlayerAudience audience = gamePlayer.getAudience();
    final Component message = Message.HEAT_SEEKER_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(HEAT_SEEKER_SOUND);
  }

  private void scheduleTasks(final PlayerManager manager, final Killer player) {
    manager.applyToAllLivingInnocents(innocent -> this.handleGlowInnocent(innocent, player));
  }

  private void handleGlowInnocent(final GamePlayer innocent, final Killer owner) {
    final Location location = innocent.getLocation();
    final Location other = owner.getLocation();
    final Collection<GamePlayer> visible = owner.getHeatSeekerGlowing();
    final double distance = location.distanceSquared(other);
    final MetadataManager metadata = owner.getMetadataManager();
    if (distance < HEAT_SEEKER_RADIUS * HEAT_SEEKER_RADIUS) {
      visible.add(innocent);
      metadata.setEntityGlowing(innocent, ChatColor.RED, true);
    } else if (visible.contains(innocent)) {
      visible.remove(innocent);
      metadata.setEntityGlowing(innocent, ChatColor.RED, false);
    }
  }
}
