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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class Tracker extends SurvivorGadget {

  public Tracker() {
    super(
      "tracker",
      GameProperties.TRACKER_COST,
      ItemFactory.createGadget("tracker", GameProperties.TRACKER_MATERIAL, Message.TRACKER_NAME.build(), Message.TRACKER_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    manager.applyToKillers(killer -> this.handleGlowing(killer, player));

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.TRACKER_SOUND);

    return false;
  }

  private void handleGlowing(final GamePlayer killer, final GamePlayer player) {
    final Location origin = player.getLocation();
    final Location killerLocation = killer.getLocation();
    final double distance = origin.distanceSquared(killerLocation);
    final MetadataManager metadata = player.getMetadataManager();
    final PlayerAudience audience = player.getAudience();
    final double radius = GameProperties.TRACKER_RADIUS;
    if (distance < radius * radius) {
      metadata.setEntityGlowing(killer, ChatColor.DARK_PURPLE, true);
      audience.sendMessage(Message.TRACKER_ACTIVATE.build());
    } else {
      audience.sendMessage(Message.TRACKER_DEACTIVATE.build());
    }
  }
}
