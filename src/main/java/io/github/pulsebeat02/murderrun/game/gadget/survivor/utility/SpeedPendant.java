package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ItemFactory;
import org.bukkit.Material;

public final class SpeedPendant extends SurvivorGadget {

  public SpeedPendant() {
    super(
        "speed_pendant",
        Material.FEATHER,
        Message.SPEED_PENDANT_NAME.build(),
        empty(),
        48,
        ItemFactory::createSpeedPendant);
  }
}
