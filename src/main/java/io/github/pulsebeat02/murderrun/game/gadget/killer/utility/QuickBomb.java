package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class QuickBomb extends KillerGadget {

  public QuickBomb() {
    super(
        "quick_bomb",
        Material.TNT,
        Locale.QUICK_BOMB_TRAP_NAME.build(),
        Locale.QUICK_BOMB_TRAP_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(this::spawnPrimedTnt);
  }

  private void spawnPrimedTnt(final Survivor survivor) {
    final Location location = survivor.getLocation();
    final World world = requireNonNull(location.getWorld());
    world.spawn(location, TNTPrimed.class, tnt -> tnt.setFuseTicks(40));
  }
}
