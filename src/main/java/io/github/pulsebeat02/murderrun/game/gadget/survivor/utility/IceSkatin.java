package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;

public final class IceSkatin extends SurvivorGadget {

  public IceSkatin() {
    super(
      "ice_skatin",
      Material.OAK_BOAT,
      Message.ICE_SKATIN_NAME.build(),
      Message.ICE_SKATIN_LORE.build(),
      GameProperties.ICE_SKATIN_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final Game game, final GamePlayer player, final Item item, final boolean remove) {
    super.onGadgetDrop(game, player, item, true);

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final Boat boat = (Boat) world.spawnEntity(location, EntityType.BOAT);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnIceUnderBoat(boat), 0L, 2L);
    scheduler.scheduleTask(boat::remove, GameProperties.ICE_SKATIN_DURATION);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.ICE_SKATIN_SOUND);

    return false;
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
