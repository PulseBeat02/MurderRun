package io.github.pulsebeat02.murderrun.game.gadget.survivor.tool;

import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import org.bukkit.Material;

public final class Shield extends SurvivorGadget {

  public Shield() {
    super(
        "shield",
        Material.SHIELD,
        Locale.SHIELD_TRAP_NAME.build(),
        Locale.SHIELD_TRAP_LORE.build(),
        16,
        stack -> ItemUtils.setDurability(stack, 5));
  }
}
