package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

public final class SpeedPendant extends SurvivorGadget {

  public SpeedPendant() {
    super(
        "speed_pendant",
        Material.WHITE_DYE,
        Locale.SPEED_PENDANT_NAME.build(),
        empty(),
        48,
        stack -> {
          final ItemMeta meta = requireNonNull(stack.getItemMeta());
          final NamespacedKey key = Attribute.GENERIC_MOVEMENT_SPEED.getKey();
          final AttributeModifier modifier =
              new AttributeModifier(key, 0.2, Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
          meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier);
        });
  }
}
