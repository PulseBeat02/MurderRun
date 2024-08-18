package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.ComponentUtils;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractGadget implements Gadget {

  private final String name;
  private final int cost;
  private final ItemStack gadget;

  public AbstractGadget(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final int cost) {
    this(name, material, itemName, itemLore, cost, null);
  }

  public AbstractGadget(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final int cost,
      final @Nullable Consumer<ItemStack> consumer) {
    this.name = name;
    this.cost = cost;
    this.gadget = this.constructItemStack(name, material, itemName, itemLore, consumer);
  }

  @Override
  public ItemStack constructItemStack(
      @UnderInitialization AbstractGadget this,
      final String pdcName,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final @Nullable Consumer<ItemStack> consumer) {

    requireNonNull(pdcName);
    requireNonNull(itemName);
    requireNonNull(itemLore);
    requireNonNull(material);

    final String name = ComponentUtils.serializeComponentToLegacyString(itemName);
    final List<Component> lore = ComponentUtils.wrapLoreLines(itemLore, 40);
    final List<String> rawLore = new ArrayList<>();
    for (final Component component : lore) {
      rawLore.add(ComponentUtils.serializeComponentToLegacyString(component));
    }

    final ItemStack stack = new ItemStack(material);
    final ItemMeta meta = requireNonNull(stack.getItemMeta());
    ItemUtils.setPersistentDataAttribute(
        stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING, pdcName);
    meta.setDisplayName(name);
    meta.setLore(rawLore);

    final Attribute attribute = Attribute.GENERIC_MOVEMENT_SPEED;
    final NamespacedKey key = attribute.getKey();
    final Operation operation = Operation.ADD_NUMBER;
    final EquipmentSlotGroup group = EquipmentSlotGroup.ANY;
    final AttributeModifier modifier = new AttributeModifier(key, 0, operation, group);
    meta.addAttributeModifier(attribute, modifier);

    meta.addItemFlags(
        ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    stack.setItemMeta(meta);

    if (consumer != null) {
      consumer.accept(stack);
    }

    return stack;
  }

  @Override
  public void onGadgetNearby(final Game game, final GamePlayer activator) {}

  @Override
  public void onGadgetRightClick(
      final Game game, final PlayerInteractEvent event, final boolean remove) {
    final Player player = event.getPlayer();
    if (remove) {
      final PlayerInventory inventory = player.getInventory();
      final ItemStack stack = inventory.getItemInMainHand();
      stack.setType(Material.AIR);
    } else {
      event.setCancelled(true);
    }
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    final Item item = event.getItemDrop();
    item.setUnlimitedLifetime(true);
    item.setPickupDelay(Integer.MAX_VALUE);
    if (remove) {
      item.remove();
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
  public @NonNull MerchantRecipe createRecipe() {

    requireNonNull(this.gadget);

    final ItemStack ingredient = new ItemStack(Material.NETHER_STAR, this.cost);
    final int uses = Integer.MAX_VALUE;
    final MerchantRecipe recipe = new MerchantRecipe(this.gadget, uses);
    recipe.addIngredient(ingredient);

    return recipe;
  }
}
