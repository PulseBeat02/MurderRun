package io.github.pulsebeat02.murderrun.game.gadget.survivor.armor;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public abstract class SurvivorGear extends SurvivorGadget {

  public SurvivorGear(final String name, final Material material, final Component itemName) {
    super(name, material, itemName, empty(), GameProperties.SURVIVOR_GEAR_COST, ItemFactory::createSurvivorGear);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return true;
  }
}
