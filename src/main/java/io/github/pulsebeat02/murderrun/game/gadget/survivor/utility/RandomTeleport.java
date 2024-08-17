package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class RandomTeleport extends SurvivorGadget {

  public RandomTeleport() {
    super(
        "random_teleport",
        Material.GOLDEN_CARROT,
        Message.TP_ME_AWAY_FROM_HERE_NAME.build(),
        Message.TP_ME_AWAY_FROM_HERE_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
    final World world = requireNonNull(first.getWorld());
    final Location temp = new Location(world, coords[0], 0, coords[1]);
    final Block block = world.getHighestBlockAt(temp);
    final Location top = block.getLocation();
    player.teleport(top);
  }
}
