/*

MIT License

Copyright (c) 2025 Brandon Li

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
package me.brandonli.murderrun.game.ability.survivor;

import java.util.Collection;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.map.part.CarPart;
import me.brandonli.murderrun.game.map.part.PartsManager;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class PartSniffer extends SurvivorAbility {

  private static final String PART_SNIFFER_NAME = "part_sniffer";

  public PartSniffer(final Game game) {
    super(
      game,
      PART_SNIFFER_NAME,
      ItemFactory.createAbility(PART_SNIFFER_NAME, Message.PART_SNIFFER_NAME.build(), Message.PART_SNIFFER_LORE.build(), 1)
    );
  }

  @Override
  public void start() {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    manager.applyToLivingSurvivors(participant -> {
      if (!participant.hasAbility(PART_SNIFFER_NAME)) {
        return;
      }
      final Survivor survivor = (Survivor) participant;
      final StrictPlayerReference reference = StrictPlayerReference.of(survivor);
      final GameScheduler scheduler = game.getScheduler();
      scheduler.scheduleRepeatedTask(() -> this.handleTrapSniffing(game, survivor), 0, 2 * 20L, reference);
    });
  }

  private void handleTrapSniffing(final Game game, final Survivor player) {
    final Location origin = player.getLocation();
    final GameMap map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final java.util.Map<String, CarPart> parts = manager.getParts();
    final Collection<CarPart> stacks = parts.values();
    final Collection<org.bukkit.entity.Item> set = player.getGlowingCarParts();
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
