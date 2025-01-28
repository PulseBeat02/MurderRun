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
import io.github.pulsebeat02.murderrun.game.map.GameMap;
import io.github.pulsebeat02.murderrun.game.map.part.CarPart;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.PlayerReference;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.SchedulerReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class PartSniffer extends SurvivorGadget {

  public PartSniffer() {
    super(
      "part_sniffer",
      Material.TRIPWIRE_HOOK,
      Message.PART_SNIFFER_NAME.build(),
      Message.PART_SNIFFER_LORE.build(),
      GameProperties.PART_SNIFFER_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    if (!(player instanceof final Survivor survivor)) {
      return true;
    }
    item.remove();

    final SchedulerReference reference = PlayerReference.of(survivor);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleTrapSniffing(game, survivor), 0, 2 * 20L, reference);

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.PART_SNIFFER_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GameProperties.PART_SNIFFER_SOUND);

    return false;
  }

  private void handleTrapSniffing(final Game game, final Survivor player) {
    final Location origin = player.getLocation();
    final GameMap map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final java.util.Map<String, CarPart> parts = manager.getParts();
    final Collection<CarPart> stacks = parts.values();
    final Collection<Item> set = player.getGlowingCarParts();
    final double radius = GameProperties.PART_SNIFFER_RADIUS;
    for (final CarPart stack : stacks) {
      final Location location = stack.getLocation();
      final Item entity = stack.getItem();
      final double distance = origin.distanceSquared(location);
      final MetadataManager metadata = player.getMetadataManager();
      if (distance < radius * radius) {
        set.add(entity);
        metadata.setEntityGlowing(entity, ChatColor.RED, true);
      } else if (set.contains(entity)) {
        set.remove(entity);
        metadata.setEntityGlowing(entity, ChatColor.RED, false);
      }
    }
  }
}
