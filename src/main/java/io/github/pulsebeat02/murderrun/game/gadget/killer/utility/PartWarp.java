package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class PartWarp extends KillerGadget {

  public PartWarp() {
    super(
        "part_warp",
        Material.REPEATER,
        Locale.PART_WARP_TRAP_NAME.build(),
        Locale.PART_WARP_TRAP_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);
  }
}
