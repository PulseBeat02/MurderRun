package io.github.pulsebeat02.murderrun.game.gadget.survivor.tool;

import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import org.bukkit.Material;

public final class Excavator extends SurvivorGadget {

  public Excavator() {
    super(
        "excavator",
        Material.DIAMOND_PICKAXE,
        Locale.EXCAVATOR_TRAP_NAME.build(),
        Locale.EXCAVATOR_TRAP_LORE.build(),
        32,
        stack -> {
          ItemUtils.setDurability(stack, 9);
        });
  }
}
