package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Item;

public final class Expander extends KillerGadget {

  public Expander() {
    super("expander", Material.BROWN_MUSHROOM, Message.EXPANDER_NAME.build(), Message.EXPANDER_LORE.build(), GameProperties.EXPANDER_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    final double scale = GameProperties.EXPANDER_SCALE;
    final AttributeInstance instance = requireNonNull(player.getAttribute(Attribute.GENERIC_SCALE));
    instance.setBaseValue(scale);
    item.remove();
    return false;
  }
}
