package io.github.pulsebeat02.murderrun.game.map.part;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.utils.ItemFactory;
import java.util.UUID;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class CarPart {

  private final String uuid;
  private final ItemStack stack;

  private Location location;
  private boolean pickedUp;
  private Item item;
  private @Nullable Item cursedNote;

  public CarPart(final Location location) {
    final UUID uuid = UUID.randomUUID();
    this.uuid = uuid.toString();
    this.location = location;
    this.stack = ItemFactory.createCarPart(this.uuid);
  }

  public Item spawn() {
    final World world = requireNonNull(this.location.getWorld());
    final Item item = world.dropItem(this.location, this.stack);
    final Location microOptimized = item.getLocation();
    this.setLocation(microOptimized);
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
