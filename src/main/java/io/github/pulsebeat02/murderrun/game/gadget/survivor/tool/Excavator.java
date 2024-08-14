package io.github.pulsebeat02.murderrun.game.gadget.survivor.tool;

import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Material;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public final class Excavator extends SurvivorGadget {

  public Excavator() {
    super(
        "excavator",
        Material.DIAMOND_PICKAXE,
        Locale.EXCAVATOR_TRAP_NAME.build(),
        Locale.EXCAVATOR_TRAP_LORE.build(),
        32,
        stack -> {
          final ItemMeta meta = stack.getItemMeta();
          if (meta instanceof final Damageable damageable) {
            final Material material = stack.getType();
            final int max = material.getMaxDurability();
            final int damage = max - 9;
            damageable.setDamage(damage);
          }
          stack.setItemMeta(meta);
        });
  }
}
