package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetSettings;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

public final class RandomTeleport extends SurvivorGadget {

  public RandomTeleport() {
    super(
        "random_teleport",
        Material.GOLDEN_CARROT,
        Message.TP_ME_AWAY_FROM_HERE_NAME.build(),
        Message.TP_ME_AWAY_FROM_HERE_LORE.build(),
        GadgetSettings.RANDOM_TELEPORT_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
    final World world = requireNonNull(first.getWorld());
    final Location temp = new Location(world, coords[0], 0, coords[1]);
    final Block block = world.getHighestBlockAt(temp);
    final Location top = block.getLocation();
    final Location teleport = top.add(0, 1, 0);
    player.teleport(teleport);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GadgetSettings.RANDOM_TELEPORT_SOUND);

    return false;
  }
}
