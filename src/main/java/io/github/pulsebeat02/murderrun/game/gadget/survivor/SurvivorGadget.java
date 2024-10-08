package io.github.pulsebeat02.murderrun.game.gadget.survivor;

import io.github.pulsebeat02.murderrun.game.gadget.AbstractGadget;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SurvivorGadget extends AbstractGadget implements SurvivorApparatus {

  public SurvivorGadget(final String name, final Material material, final Component itemName, final Component itemLore, final int cost) {
    super(name, material, itemName, itemLore, cost);
  }

  public SurvivorGadget(
    final String name,
    final Material material,
    final Component itemName,
    final Component itemLore,
    final int cost,
    final @Nullable Consumer<ItemStack> consumer
  ) {
    super(name, material, itemName, itemLore, cost, consumer);
  }
}
