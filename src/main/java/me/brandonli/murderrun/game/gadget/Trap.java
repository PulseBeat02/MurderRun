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
      final Color color) {
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
