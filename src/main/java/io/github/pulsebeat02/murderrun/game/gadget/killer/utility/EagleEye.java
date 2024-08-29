package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

public final class EagleEye extends KillerGadget {

  private static final int EAGLE_EYE_DURATION = 10 * 20;
  private static final String EAGLE_EYE_SOUND = "entity.phantom.flap";

  public EagleEye() {
    super(
        "eagle_eye",
        Material.FEATHER,
        Message.EAGLE_EYE_NAME.build(),
        Message.EAGLE_EYE_LORE.build(),
        16);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location[] corners = arena.getCorners();
    final Location average = MapUtils.getAverageLocation(corners[0], corners[1]);
    final World world = requireNonNull(average.getWorld());

    final Block highest = world.getHighestBlockAt(average);
    final Location location = highest.getLocation();
    final Location teleport = location.add(0, 50, 0);

    final Location previous = player.getLocation();
    player.setGravity(false);
    player.teleport(teleport);
    player.setAllowFlight(true);

    final float before = player.getFlySpeed();
    player.setFlySpeed(0.0f);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(EAGLE_EYE_SOUND);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.resetState(player, previous, before), EAGLE_EYE_DURATION);

    return false;
  }

  private void resetState(
      final GamePlayer gamePlayer, final Location previous, final float flySpeed) {
    gamePlayer.teleport(previous);
    gamePlayer.setGravity(false);
    gamePlayer.setAllowFlight(true);
    gamePlayer.setFlySpeed(flySpeed);
  }
}
