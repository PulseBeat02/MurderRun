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
package me.brandonli.murderrun.game.gadget;

import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.packet.GadgetNearbyPacket;
import me.brandonli.murderrun.game.gadget.packet.GadgetRightClickPacket;
import me.brandonli.murderrun.utils.item.Item;

public abstract class AbstractGadget implements Gadget {

  private final String name;
  private final int cost;
  private final Item.Builder builder;

  public AbstractGadget(final String name, final int cost, final Item.Builder builder) {
    this.name = name;
    this.cost = cost;
    this.builder = builder;
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
  public Item.Builder getStackBuilder() {
    return this.builder;
  }

  @Override
  public String getId() {
    return this.name;
  }

  @Override
  public int getPrice() {
    return this.cost;
  }
}
