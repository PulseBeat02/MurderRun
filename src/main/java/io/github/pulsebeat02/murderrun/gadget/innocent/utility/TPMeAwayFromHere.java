package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class TPMeAwayFromHere extends MurderGadget {

  public TPMeAwayFromHere() {
    super(
        "tp_me_away_from_here",
        Material.GOLDEN_CARROT,
        Locale.TP_ME_AWAY_FROM_HERE_TRAP_NAME.build(),
        Locale.TP_ME_AWAY_FROM_HERE_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    super.onDropEvent(game, event);

    final Player player = event.getPlayer();
    final MurderSettings settings = game.getSettings();
    final MurderArena arena = settings.getArena();
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
    final World world = first.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    final Location temp = new Location(world, coords[0], 0, coords[1]);
    final Block block = world.getHighestBlockAt(temp);
    final Location top = block.getLocation();
    player.teleport(top);
  }
}
