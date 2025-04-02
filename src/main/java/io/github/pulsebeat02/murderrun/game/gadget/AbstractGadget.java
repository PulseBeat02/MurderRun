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
package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetNearbyPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetRightClickPacket;
import io.github.pulsebeat02.murderrun.utils.item.Item;

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
