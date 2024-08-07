package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.utils.ComponentUtils;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import io.github.pulsebeat02.murderrun.utils.Keys;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Gadget {

  private final String name;
  private final ItemStack gadget;

  public Gadget(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore) {
    this(name, material, itemName, itemLore, null);
  }

  public Gadget(
      final String name,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final @Nullable Consumer<ItemStack> consumer) {
    this.name = name;
    this.gadget = this.constructItemStack(name, material, itemName, itemLore, consumer);
  }

  public ItemStack constructItemStack(
      @UnderInitialization Gadget this,
      final String pdcName,
      final Material material,
      final Component itemName,
      final Component itemLore,
      final @Nullable Consumer<ItemStack> consumer) {

    requireNonNull(pdcName);
    requireNonNull(itemName);
    requireNonNull(itemLore);
    requireNonNull(material);

    final String name = ComponentUtils.serializeComponentToLegacy(itemName);
    final String rawLore = ComponentUtils.serializeComponentToLegacy(itemLore);
    final List<String> lore = List.of(rawLore);
    final ItemStack stack = new ItemStack(material);
    final ItemMeta meta = requireNonNull(stack.getItemMeta());
    ItemUtils.setData(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING, pdcName);
    meta.setDisplayName(name);
    meta.setLore(lore);
    stack.setItemMeta(meta);

    if (consumer != null) {
      consumer.accept(stack);
    }

    return stack;
  }

  public void onGadgetNearby(final Game game, final GamePlayer activator) {}

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

  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    final Item item = event.getItemDrop();
    item.setUnlimitedLifetime(true);
    item.setPickupDelay(Integer.MAX_VALUE);
    if (remove) {
      item.remove();
    }
  }

  public ItemStack getGadget() {
    return this.gadget;
  }

  public String getName() {
    return this.name;
  }
}
