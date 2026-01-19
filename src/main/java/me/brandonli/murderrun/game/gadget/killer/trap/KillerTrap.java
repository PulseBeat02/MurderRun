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
package me.brandonli.murderrun.game.gadget.killer.trap;

import java.awt.Color;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.gadget.Trap;
import me.brandonli.murderrun.game.gadget.killer.KillerDevice;
import me.brandonli.murderrun.game.gadget.packet.GadgetNearbyPacket;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.utils.item.Item;
import net.kyori.adventure.text.Component;

public abstract class KillerTrap extends Trap implements KillerDevice {

  public KillerTrap(
      final String name,
      final int cost,
      final Item.Builder item,
      final Component announcement,
      final Color color) {
    super(name, cost, item, announcement, color);
  }

  @Override
  public void onGadgetNearby(final GadgetNearbyPacket packet) {
    super.onGadgetNearby(packet);
    final Component subtitle = this.getAnnouncement();
    if (subtitle != null) {
      final Game game = packet.getGame();
      final GamePlayerManager manager = game.getPlayerManager();
      manager.sendMessageToAllMurderers(subtitle);
    }
  }
}
