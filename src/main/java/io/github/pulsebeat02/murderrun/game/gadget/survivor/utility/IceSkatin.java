package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class IceSkatin extends SurvivorGadget {

  public IceSkatin() {
    super(
        "ice_skatin",
        Material.OAK_BOAT,
        Locale.ICE_SKATIN_TRAP_NAME.build(),
        Locale.ICE_SKATIN_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final Boat boat = (Boat) world.spawnEntity(location, EntityType.BOAT);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.spawnIceUnderBoat(boat), 5L);
  }

  private void spawnIceUnderBoat(final Boat boat) {
    final Location boatLocation = boat.getLocation();
    final Block blockUnderBoat = boatLocation.subtract(0, 1, 0).getBlock();
    final Material under = blockUnderBoat.getType();
    if (under != Material.ICE) {
      blockUnderBoat.setType(Material.ICE);
    }
  }
}
