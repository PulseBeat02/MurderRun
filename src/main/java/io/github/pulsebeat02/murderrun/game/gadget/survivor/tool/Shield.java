package io.github.pulsebeat02.murderrun.game.gadget.survivor.tool;

import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Material;

public final class Shield extends SurvivorGadget {

  public Shield() {
    super(
      "shield",
      Material.SHIELD,
      Message.SHIELD_NAME.build(),
      Message.SHIELD_LORE.build(),
      GameProperties.SHIELD_COST,
      ItemFactory::createShield
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return true;
  }
}
