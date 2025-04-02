/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.map.part;

import static java.util.Objects.requireNonNull;

import java.util.UUID;
import me.brandonli.murderrun.utils.item.ItemFactory;
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
