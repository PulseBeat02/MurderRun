/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.map.part;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
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
    if (this.item != null) {
      return this.item.getLocation();
    }
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

  public void setCursed(final @Nullable Item cursedNote) {
    this.cursedNote = cursedNote;
  }
}
