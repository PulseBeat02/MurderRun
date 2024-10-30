package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

public final class Miniaturizer extends SurvivorGadget {

  public Miniaturizer() {
    super(
      "miniaturizer",
      Material.RED_MUSHROOM,
      Message.MINIATURIZER_NAME.build(),
      Message.MINIATURIZER_LORE.build(),
      GameProperties.MINIATURIZER_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final GamePlayer player = packet.getPlayer();
    final double scale = GameProperties.MINIATURIZER_SCALE;
    final AttributeInstance instance = player.getAttribute(Attribute.GENERIC_SCALE);
    instance.setBaseValue(scale);
    return true;
  }
}
