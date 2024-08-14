package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Material;

public final class EMPBlast extends KillerGadget {

  public EMPBlast() {
    super(
        "emp_blast",
        Material.SNOW_BLOCK,
        Locale.EMP_BLAST_TRAP_NAME.build(),
        Locale.EMP_BLAST_TRAP_LORE.build(),
        96);
  }
}
