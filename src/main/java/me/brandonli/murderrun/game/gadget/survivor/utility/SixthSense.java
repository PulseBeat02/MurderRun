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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import java.util.Collection;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class SixthSense extends SurvivorGadget {

  public SixthSense() {
    super(
      "sixth_sense",
      GameProperties.SIXTH_SENSE_COST,
      ItemFactory.createGadget(
        "sixth_sense",
        GameProperties.SIXTH_SENSE_MATERIAL,
        Message.SIXTH_SENSE_NAME.build(),
        Message.SIXTH_SENSE_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final GamePlayerManager manager = game.getPlayerManager();
    if (!(player instanceof final Survivor survivor)) {
      return true;
    }
    item.remove();

    final GameScheduler scheduler = game.getScheduler();
    final NullReference reference = NullReference.of();
    scheduler.scheduleRepeatedTask(() -> this.handleKillers(manager, survivor), 0, 2 * 20L, reference);

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.SIXTH_SENSE_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GameProperties.SIXTH_SENSE_SOUND);

    return false;
  }

  private void handleKillers(final GamePlayerManager manager, final Survivor player) {
    manager.applyToKillers(murderer -> this.handleGlowMurderer(murderer, player));
  }

  private void handleGlowMurderer(final GamePlayer killer, final Survivor survivor) {
    final Location location = survivor.getLocation();
    final Location other = killer.getLocation();
    final Collection<GamePlayer> visible = survivor.getGlowingKillers();
    final double distance = location.distanceSquared(other);
    final MetadataManager metadata = survivor.getMetadataManager();
    final double radius = GameProperties.SIXTH_SENSE_RADIUS;
    if (distance < radius * radius) {
      visible.add(killer);
      metadata.setEntityGlowing(killer, ChatColor.BLUE, true);
    } else if (visible.contains(killer)) {
      visible.remove(killer);
      metadata.setEntityGlowing(killer, ChatColor.BLUE, false);
    }
  }
}
