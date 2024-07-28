package io.github.pulsebeat02.murderrun.gadget.innocent.armor;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import static net.kyori.adventure.text.Component.empty;

public abstract sealed class SurvivorGear extends MurderGadget permits SurvivorBoots, SurvivorChestplate, SurvivorHelmet, SurvivorLeggings {

    public SurvivorGear(final String name, final Material material, final Component itemName) {
        super(name, material, itemName, empty(), stack -> stack.addEnchantment(Enchantment.PROTECTION, 1));
    }
}
