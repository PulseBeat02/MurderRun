package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.Participant;
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

public final class FloorIsLava extends KillerGadget {

  public FloorIsLava() {
    super(
        "floor_is_lava",
        Material.MAGMA_BLOCK,
        Message.THE_FLOOR_IS_LAVA_NAME.build(),
        Message.THE_FLOOR_IS_LAVA_LORE.build(),
        64);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    if (!(player instanceof final Killer killer)) {
      return true;
    }

    scheduler.scheduleRepeatedTask(
        () -> this.handleSurvivors(manager, scheduler, killer), 0, 6 * 20L);

    manager.applyToAllParticipants(this::sendFloorIsLavaMessage);
    manager.playSoundForAllParticipants(GadgetConstants.FLOOR_IS_LAVA_SOUND);

    return false;
  }

  private void handleSurvivors(
      final PlayerManager manager, final GameScheduler scheduler, final Killer killer) {
    manager.applyToAllLivingInnocents(survivor -> this.handleMovement(scheduler, survivor, killer));
  }

  private void handleMovement(
      final GameScheduler scheduler, final GamePlayer player, final Killer killer) {
    final Location previous = player.getLocation();
    scheduler.scheduleTask(() -> this.handleLocationChecking(previous, player, killer), 5 * 20L);
  }

  private void handleLocationChecking(
      final Location previous, final GamePlayer player, final Killer killer) {
    final Location newLocation = player.getLocation();
    final Collection<GamePlayer> glowing = killer.getFloorIsLavaGlowing();
    final MetadataManager metadata = killer.getMetadataManager();
    if (this.checkLocationSame(previous, newLocation)) {
      glowing.add(player);
      metadata.setEntityGlowing(player, ChatColor.RED, true);
    } else if (glowing.contains(player)) {
      glowing.remove(player);
      metadata.setEntityGlowing(player, ChatColor.RED, false);
    }
  }

  private boolean checkLocationSame(final Location first, final Location second) {
    return first.getBlockX() == second.getBlockX()
        && first.getBlockY() == second.getBlockY()
        && first.getBlockZ() == second.getBlockZ();
  }

  private void sendFloorIsLavaMessage(final Participant participant) {
    final PlayerAudience audience = participant.getAudience();
    final Component msg = Message.THE_FLOOR_IS_LAVA_ACTIVATE.build();
    audience.sendMessage(msg);
  }
}
