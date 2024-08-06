package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public final class MedKit extends Gadget {

  public MedKit() {
    super(
        "med_kit",
        Material.SPLASH_POTION,
        Locale.MED_KIT_TRAP_NAME.build(),
        Locale.MED_KIT_TRAP_LORE.build(),
        stack -> {
          final ItemMeta meta = stack.getItemMeta();
          if (meta instanceof final PotionMeta potionMeta) {
            potionMeta.setColor(Color.RED);
            potionMeta.setBasePotionType(PotionType.STRONG_HEALING);
            stack.setItemMeta(potionMeta);
          }
        });
  }
}
