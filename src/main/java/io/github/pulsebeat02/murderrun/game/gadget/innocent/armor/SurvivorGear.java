package io.github.pulsebeat02.murderrun.game.gadget.innocent.armor;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public abstract sealed class SurvivorGear extends MurderGadget
    permits SurvivorBoots, SurvivorChestplate, SurvivorHelmet, SurvivorLeggings {

  public SurvivorGear(final String name, final Material material, final Component itemName) {
    super(
        name,
        material,
        itemName,
        empty(),
        stack -> stack.addEnchantment(Enchantment.PROTECTION, 1));
  }
}
