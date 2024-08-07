package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Material;

public final class WarpDistort extends KillerGadget {

  public WarpDistort() {
    super(
        "warp_distort",
        Material.CHORUS_FRUIT,
        Locale.WARP_DISTORT_TRAP_NAME.build(),
        Locale.WARP_DISTORT_TRAP_LORE.build());
  }
}
