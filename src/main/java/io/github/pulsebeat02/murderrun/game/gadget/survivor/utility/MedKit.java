package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public final class MedKit extends SurvivorGadget {

  public MedKit() {
    super(
        "med_kit",
        Material.SPLASH_POTION,
        Message.MED_KIT_NAME.build(),
        Message.MED_KIT_LORE.build(),
        16,
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
