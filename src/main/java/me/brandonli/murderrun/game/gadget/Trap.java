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
package me.brandonli.murderrun.game.gadget;

import java.awt.Color;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.packet.GadgetNearbyPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Item;

public abstract class Trap extends AbstractGadget {

  private final Component announcement;
  private final Color color;

  public Trap(
    final String name,
    final int cost,
    final me.brandonli.murderrun.utils.item.Item.Builder builder,
    final Component announcement,
    final Color color
  ) {
    super(name, cost, builder);
    this.announcement = announcement;
    this.color = color;
  }

  public Component getAnnouncement() {
    return this.announcement;
  }

  public Color getColor() {
    return this.color;
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final Item item = packet.getItem();
    final GameScheduler scheduler = game.getScheduler();
    item.setUnlimitedLifetime(true);
    item.setPickupDelay(Integer.MAX_VALUE);
    this.scheduleParticleTask(item, scheduler);
    return false;
  }

  @Override
  public void onGadgetNearby(final GadgetNearbyPacket packet) {
    final Item item = packet.getItem();
    final Game game = packet.getGame();
    final GamePlayer activator = packet.getActivator();
    this.onTrapActivate(game, activator, item);
    item.remove();
  }

  private void scheduleParticleTask(final Item item, final GameScheduler scheduler) {
    final int r = this.color.getRed();
    final int g = this.color.getGreen();
    final int b = this.color.getBlue();
    final org.bukkit.Color bukkitColor = org.bukkit.Color.fromRGB(r, g, b);
    scheduler.scheduleParticleTaskUntilDeath(item, bukkitColor);
  }

  public abstract void onTrapActivate(final Game game, final GamePlayer activee, final Item item);
}
