package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

public final class SpeedPendant extends MurderGadget {

  public SpeedPendant() {
    super(
        "speed_pendant",
        Material.WHITE_DYE,
        Locale.SPEED_PENDANT_NAME.build(),
        empty(),
        stack -> {

          final ItemMeta meta = stack.getItemMeta();
          if (meta == null) {
            throw new AssertionError("Failed to create speed pendant!");
          }

          final NamespacedKey key = Attribute.GENERIC_MOVEMENT_SPEED.getKey();
          final AttributeModifier modifier = new AttributeModifier(key, 0.2, Operation.ADD_NUMBER,
              EquipmentSlotGroup.ANY);
          meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier);
        });
  }
}
