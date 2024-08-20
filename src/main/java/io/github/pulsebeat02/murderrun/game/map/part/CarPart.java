package io.github.pulsebeat02.murderrun.game.map.part;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class CarPart {

  private final String uuid;
  private final ItemStack stack;

  private Location location;
  private boolean pickedUp;
  private Item item;
  private @Nullable Item cursedNote;

  public CarPart(final Location location) {
    this.uuid = UUID.randomUUID().toString();
    this.location = location;
    this.stack = this.createItemStack();
  }

  private ItemStack createItemStack(@UnderInitialization CarPart this) {
    final ItemStack stack = new ItemStack(Material.DIAMOND);
    this.customize(stack);
    return stack;
  }

  private void customize(@UnderInitialization CarPart this, final ItemStack stack) {
    final ItemMeta meta = requireNonNull(stack.getItemMeta());
    this.tagData(stack);
    this.setLore(meta);
    this.changeProperties(meta);
    stack.setItemMeta(meta);
  }

  private void tagData(@UnderInitialization CarPart this, final ItemStack stack) {
    if (this.uuid != null) {
      ItemUtils.setPersistentDataAttribute(
          stack, Keys.CAR_PART_UUID, PersistentDataType.STRING, this.uuid);
    }
  }

  private void setLore(@UnderInitialization CarPart this, final ItemMeta meta) {
    if (!meta.hasLore()) {
      final List<Component> components = List.of(Message.CAR_PART_ITEM_LORE.build());
      final List<String> lore = components.stream()
          .map(AdventureUtils::serializeComponentToLegacyString)
          .toList();
      meta.setLore(lore);
    }
  }

  private void changeProperties(@UnderInitialization CarPart this, final ItemMeta meta) {
    final Component component = Message.CAR_PART_ITEM_NAME.build();
    final String raw = AdventureUtils.serializeComponentToLegacyString(component);
    final int id = RandomUtils.generateInt(1, 6);
    meta.setDisplayName(raw);
    meta.setCustomModelData(id);
  }

  public Item spawn() {
    final World world = requireNonNull(this.location.getWorld());
    final Item item = world.dropItemNaturally(this.location, this.stack);
    this.customizeItemEntity(item);
    this.item = item;
    return item;
  }

  public Item getItem() {
    return this.item;
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

  public boolean isCursed() {
    if (this.cursedNote != null && this.cursedNote.isDead()) {
      this.cursedNote = null;
    }
    return this.cursedNote != null;
  }

  public void setCursed(final Item cursedNote) {
    this.cursedNote = cursedNote;
  }
}
