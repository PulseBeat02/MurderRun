package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Item;

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
    final Item item = packet.getItem();
    final double scale = GameProperties.MINIATURIZER_SCALE;
    final AttributeInstance instance = requireNonNull(player.getAttribute(Attribute.SCALE));
    instance.setBaseValue(scale);
    item.remove();
    return false;
  }
}
