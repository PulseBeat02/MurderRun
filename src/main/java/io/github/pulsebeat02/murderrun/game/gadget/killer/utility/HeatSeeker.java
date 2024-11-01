package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
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
import org.bukkit.entity.Item;

public final class HeatSeeker extends KillerGadget {

  public HeatSeeker() {
    super(
      "heat_seeker",
      Material.BLAZE_ROD,
      Message.HEAT_SEEKER_NAME.build(),
      Message.HEAT_SEEKER_LORE.build(),
      GameProperties.HEAT_SEEKER_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final PlayerManager manager = game.getPlayerManager();
    if (!(player instanceof final Killer killer)) {
      return true;
    }
    item.remove();

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.scheduleTasks(manager, killer), 0, 2 * 20L);

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.HEAT_SEEKER_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GameProperties.HEAT_SEEKER_SOUND);

    return false;
  }

  private void scheduleTasks(final PlayerManager manager, final Killer player) {
    manager.applyToLivingSurvivors(innocent -> this.handleGlowInnocent(innocent, player));
  }

  private void handleGlowInnocent(final GamePlayer innocent, final Killer owner) {
    final Location location = innocent.getLocation();
    final Location other = owner.getLocation();
    final Collection<GamePlayer> visible = owner.getHeatSeekerGlowing();
    final double distance = location.distanceSquared(other);
    final MetadataManager metadata = owner.getMetadataManager();
    final double radius = GameProperties.HEAT_SEEKER_RADIUS;
    if (distance < radius * radius) {
      visible.add(innocent);
      metadata.setEntityGlowing(innocent, ChatColor.RED, true);
    } else if (visible.contains(innocent)) {
      visible.remove(innocent);
      metadata.setEntityGlowing(innocent, ChatColor.RED, false);
    }
  }
}
