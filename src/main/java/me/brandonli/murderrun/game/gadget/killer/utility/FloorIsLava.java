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
package me.brandonli.murderrun.game.gadget.killer.utility;

import java.util.Collection;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Killer;
import me.brandonli.murderrun.game.player.Participant;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class FloorIsLava extends KillerGadget {

  public FloorIsLava() {
    super(
      "floor_is_lava",
      GameProperties.FLOOR_IS_LAVA_COST,
      ItemFactory.createGadget(
        "floor_is_lava",
        GameProperties.FLOOR_IS_LAVA_MATERIAL,
        Message.THE_FLOOR_IS_LAVA_NAME.build(),
        Message.THE_FLOOR_IS_LAVA_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final GamePlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    if (!(player instanceof final Killer killer)) {
      return true;
    }
    item.remove();

    final StrictPlayerReference reference = StrictPlayerReference.of(killer);
    scheduler.scheduleRepeatedTask(() -> this.handleSurvivors(manager, scheduler, killer), 0, 4 * 20L, reference);

    manager.applyToAllParticipants(this::sendFloorIsLavaMessage);
    manager.playSoundForAllParticipants(GameProperties.FLOOR_IS_LAVA_SOUND);

    return false;
  }

  private void handleSurvivors(final GamePlayerManager manager, final GameScheduler scheduler, final Killer killer) {
    manager.applyToLivingSurvivors(survivor -> this.handleMovement(scheduler, survivor, killer));
  }

  private void handleMovement(final GameScheduler scheduler, final GamePlayer player, final Killer killer) {
    final Location previous = player.getLocation();
    final StrictPlayerReference reference = StrictPlayerReference.of(killer);
    scheduler.scheduleTask(() -> this.handleLocationChecking(previous, player, killer), 3 * 20L, reference);
  }

  private void handleLocationChecking(final Location previous, final GamePlayer player, final Killer killer) {
    final Location newLocation = player.getLocation();
    final Collection<GamePlayer> glowing = killer.getFloorIsLavaGlowing();
    final MetadataManager metadata = killer.getMetadataManager();
    if (this.checkLocationSame(previous, newLocation)) {
      glowing.add(player);
      metadata.setEntityGlowing(player, ChatColor.RED, true);
    } else if (glowing.contains(player)) {
      glowing.remove(player);
      metadata.setEntityGlowing(player, ChatColor.RED, false);
    }
  }

  private boolean checkLocationSame(final Location first, final Location second) {
    return (first.getBlockX() == second.getBlockX() && first.getBlockY() == second.getBlockY() && first.getBlockZ() == second.getBlockZ());
  }

  private void sendFloorIsLavaMessage(final Participant participant) {
    final PlayerAudience audience = participant.getAudience();
    final Component msg = Message.THE_FLOOR_IS_LAVA_ACTIVATE.build();
    audience.sendMessage(msg);
  }
}
