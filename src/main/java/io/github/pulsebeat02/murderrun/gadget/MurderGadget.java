package io.github.pulsebeat02.murderrun.gadget;

import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

import java.util.List;
import java.util.function.Consumer;

public abstract class MurderGadget {

    private final String name;
    private final Material material;
    private final Component itemName;
    private final Component itemLore;
    private final ItemStack gadget;

    public MurderGadget(final String name,
                        final Material material,
                        final Component itemName,
                        final Component itemLore) {
        this(name, material, itemName, itemLore, null);
    }

    public MurderGadget(final String name,
                        final Material material,
                        final Component itemName,
                        final Component itemLore,
                        final Consumer<ItemStack> consumer) {
        this.name = name;
        this.material = material;
        this.itemName = itemName;
        this.itemLore = itemLore;
        this.gadget = this.constructItemStack(consumer);
    }

    public ItemStack constructItemStack(@UnderInitialization MurderGadget this, final Consumer<ItemStack> consumer) {

        if (this.itemName == null || this.itemLore == null || this.material == null) {
            throw new AssertionError("Failed to create ItemStack for trap!");
        }

        final String name = AdventureUtils.serializeComponentToLegacy(this.itemName);
        final String rawLore = AdventureUtils.serializeComponentToLegacy(this.itemLore);
        final List<String> lore = List.of(rawLore);
        final ItemStack stack = new ItemStack(this.material);
        final ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            throw new AssertionError("Failed to construct ItemStack for trap!");
        }

        meta.setDisplayName(name);
        meta.setLore(lore);
        stack.setItemMeta(meta);

        consumer.accept(stack);

        return stack;
    }

    public ItemStack getGadget() {
        return this.gadget;
    }

    public Component getItemLore() {
        return this.itemLore;
    }

    public Component getItemName() {
        return this.itemName;
    }

    public Material getMaterial() {
        return this.material;
    }

    public String getName() {
        return this.name;
    }
}
