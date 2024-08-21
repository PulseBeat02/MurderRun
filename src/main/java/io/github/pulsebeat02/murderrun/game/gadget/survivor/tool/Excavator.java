package io.github.pulsebeat02.murderrun.game.gadget.survivor.tool;

import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ItemFactory;
import org.bukkit.Material;

public final class Excavator extends SurvivorGadget {

  public Excavator() {
    super(
        "excavator",
        Material.DIAMOND_PICKAXE,
        Message.EXCAVATOR_NAME.build(),
        Message.EXCAVATOR_LORE.build(),
        32,
        ItemFactory::createExcavator);
  }
}
