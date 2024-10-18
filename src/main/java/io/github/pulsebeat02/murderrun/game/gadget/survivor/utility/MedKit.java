package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Material;

public final class MedKit extends SurvivorGadget {

  public MedKit() {
    super(
      "med_kit",
      Material.SPLASH_POTION,
      Message.MED_KIT_NAME.build(),
      Message.MED_KIT_LORE.build(),
      GameProperties.MED_KIT_COST,
      ItemFactory::createMedKit
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return false;
  }
}
