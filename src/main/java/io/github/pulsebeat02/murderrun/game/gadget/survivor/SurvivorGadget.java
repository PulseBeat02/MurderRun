package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SurvivorGadget extends Gadget {

  public SurvivorGadget(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore) {
    super(name, material, itemName, itemLore);
  }

  public SurvivorGadget(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      @Nullable final Consumer<ItemStack> consumer) {
    super(name, material, itemName, itemLore, consumer);
  }
}
