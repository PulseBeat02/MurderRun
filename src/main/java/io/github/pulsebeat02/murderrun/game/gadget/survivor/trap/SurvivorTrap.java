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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.Trap;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetNearbyPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorApparatus;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import java.awt.Color;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public abstract class SurvivorTrap extends Trap implements SurvivorApparatus {

  public SurvivorTrap(
    final String name,
    final Material material,
    final Component itemName,
    final Component itemLore,
    final Component announcement,
    final int cost,
    final Color color
  ) {
    super(name, material, itemName, itemLore, announcement, cost, color);
  }

  @Override
  public void onGadgetNearby(final GadgetNearbyPacket packet) {
    super.onGadgetNearby(packet);
    final Component announcement = empty();
    final Component subtitle = this.getAnnouncement();
    if (subtitle != null) {
      final Game game = packet.getGame();
      final GamePlayerManager manager = game.getPlayerManager();
      manager.showTitleForAllInnocents(announcement, subtitle);
    }
  }
}
