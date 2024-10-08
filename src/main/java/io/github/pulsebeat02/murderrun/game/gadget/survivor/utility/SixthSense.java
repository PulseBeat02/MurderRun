package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
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
import org.bukkit.entity.Item;

public final class SixthSense extends SurvivorGadget {

  public SixthSense() {
    super(
      "sixth_sense",
      Material.GOLDEN_CARROT,
      Message.SIXTH_SENSE_NAME.build(),
      Message.SIXTH_SENSE_LORE.build(),
      GameProperties.SIXTH_SENSE_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final Game game, final GamePlayer player, final Item item, final boolean remove) {
    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    if (!(player instanceof final Survivor survivor)) {
      return true;
    }

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleKillers(manager, survivor), 0, 2 * 20L);

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.SIXTH_SENSE_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GameProperties.SIXTH_SENSE_SOUND);

    return false;
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
    final double radius = GameProperties.SIXTH_SENSE_RADIUS;
    if (distance < radius * radius) {
      visible.add(killer);
      metadata.setEntityGlowing(killer, ChatColor.BLUE, true);
    } else if (visible.contains(killer)) {
      visible.remove(killer);
      metadata.setEntityGlowing(killer, ChatColor.BLUE, false);
    }
  }
}
