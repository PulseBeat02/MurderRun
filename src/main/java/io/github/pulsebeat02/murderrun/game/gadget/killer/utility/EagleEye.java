package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class EagleEye extends KillerGadget {

  public EagleEye() {
    super(
        "eagle_eye",
        Material.FEATHER,
        Message.EAGLE_EYE_NAME.build(),
        Message.EAGLE_EYE_LORE.build(),
        16);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location[] corners = arena.getCorners();
    final Location average = MapUtils.getAverageLocation(corners[0], corners[1]);
    final World world = requireNonNull(average.getWorld());

    final Block highest = world.getHighestBlockAt(average);
    final Location location = highest.getLocation();
    final Location teleport = location.add(0, 50, 0);

    final Player player = event.getPlayer();
    final Location previous = player.getLocation();
    player.setGravity(false);
    player.teleport(teleport);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.resetState(player, previous), 10 * 20L);
  }

  private void resetState(final Player player, final Location previous) {
    player.teleport(previous);
    player.setGravity(true);
  }
}
