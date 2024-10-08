package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
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
  public void onGadgetNearby(final Game game, final GamePlayer activator, final Item item) {}

  @Override
  public void onGadgetRightClick(final Game game, final PlayerInteractEvent event, final boolean remove) {
    final Player player = event.getPlayer();
    if (remove) {
      final PlayerInventory inventory = player.getInventory();
      final ItemStack stack = inventory.getItemInMainHand();
      final int amount = stack.getAmount();
      if (amount == 1) {
        inventory.setItemInMainHand(null);
      } else {
        stack.setAmount(amount - 1);
      }
    } else {
      event.setCancelled(true);
    }
  }

  @Override
  public boolean onGadgetDrop(final Game game, final GamePlayer player, final Item item, final boolean remove) {
    if (remove) {
      item.remove();
      return false;
    } else {
      return true;
    }
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
