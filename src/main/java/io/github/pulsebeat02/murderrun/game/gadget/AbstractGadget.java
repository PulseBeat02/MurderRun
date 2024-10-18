package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetNearbyPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetRightClickPacket;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractGadget implements Gadget {

  private final String name;
  private final int cost;
  private final ItemStack gadget;

  public AbstractGadget(final String name, final Material material, final Component itemName, final Component itemLore, final int cost) {
    this(name, material, itemName, itemLore, cost, null);
  }

  public AbstractGadget(
    final String name,
    final Material material,
    final Component itemName,
    final Component itemLore,
    final int cost,
    final @Nullable Consumer<ItemStack> consumer
  ) {
    this.name = name;
    this.cost = cost;
    this.gadget = ItemFactory.createGadget(name, material, itemName, itemLore, consumer);
  }

  @Override
  public void onGadgetNearby(final GadgetNearbyPacket packet) {}

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return false;
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    return false;
  }

  @Override
  public ItemStack getGadget() {
    return this.gadget;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public int getPrice() {
    return this.cost;
  }

  @Override
  public MerchantRecipe createRecipe() {
    final ItemStack ingredient = ItemFactory.createCurrency(this.cost);
    final int uses = Integer.MAX_VALUE;
    final MerchantRecipe recipe = new MerchantRecipe(requireNonNull(this.gadget), uses);
    recipe.addIngredient(ingredient);
    return recipe;
  }
}
