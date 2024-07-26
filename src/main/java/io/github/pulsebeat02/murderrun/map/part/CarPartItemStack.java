package io.github.pulsebeat02.murderrun.map.part;

import io.github.pulsebeat02.murderrun.immutable.NamespacedKeys;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class CarPartItemStack {

  private final String uuid;
  private final ItemStack stack;
  private Location location;
  private boolean pickedUp;

  public CarPartItemStack(final Location location) {
    this.uuid = UUID.randomUUID().toString();
    this.location = location;
    this.stack = this.createItemStack();
  }

  private ItemStack createItemStack(@UnderInitialization CarPartItemStack this) {
    final ItemStack stack = new ItemStack(Material.DIAMOND);
    final ItemMeta meta = stack.getItemMeta();
    if (meta == null) {
      throw new AssertionError("Unable to create car part!");
    }
    this.customize(meta);
    stack.setItemMeta(meta);
    return stack;
  }

  private void customize(@Initialized CarPartItemStack this, final ItemMeta meta) {
    this.tagData(meta);
    this.setLore(meta);
    this.changeProperties(meta);
  }

  private void tagData(final ItemMeta meta) {
    final PersistentDataContainer container = meta.getPersistentDataContainer();
    container.set(NamespacedKeys.CAR_PART_UUID, PersistentDataType.STRING, this.uuid);
  }

  private void setLore(final ItemMeta meta) {
    if (!meta.hasLore()) {
      final List<Component> components = List.of(Locale.CAR_PART_ITEM_LORE.build());
      final List<String> lore =
          components.stream().map(AdventureUtils::serializeComponentToLegacy).toList();
      meta.setLore(lore);
    }
  }

  private void changeProperties(final ItemMeta meta) {
    final Component component = Locale.CAR_PART_ITEM_NAME.build();
    final String raw = AdventureUtils.serializeComponentToLegacy(component);
    final int id = RandomUtils.generateInt(1, 6);
    meta.setDisplayName(raw);
    meta.setCustomModelData(id);
  }

  public void spawn() {
    final World world = this.location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }
    final Item item = world.dropItemNaturally(this.location, this.stack);
    this.customizeItemEntity(item);
  }

  private void customizeItemEntity(final Item item) {
    item.setUnlimitedLifetime(true);
  }

  public ItemStack getStack() {
    return this.stack;
  }

  public Location getLocation() {
    return this.location;
  }

  public void setLocation(final Location location) {
    this.location = location;
  }

  public String getUuid() {
    return this.uuid;
  }

  public boolean isPickedUp() {
    return this.pickedUp;
  }

  public void setPickedUp(final boolean pickedUp) {
    this.pickedUp = pickedUp;
  }
}
