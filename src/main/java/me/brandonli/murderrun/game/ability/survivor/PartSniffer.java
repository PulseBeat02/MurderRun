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
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class PartSniffer extends SurvivorAbility {

  private static final String PART_SNIFFER_NAME = "part_sniffer";

  public PartSniffer(final Game game) {
    super(
        game,
        PART_SNIFFER_NAME,
        ItemFactory.createAbility(
            PART_SNIFFER_NAME,
            Message.PART_SNIFFER_NAME.build(),
            Message.PART_SNIFFER_LORE.build(),
            1));
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
      scheduler.scheduleRepeatedTask(
          () -> this.handleTrapSniffing(game, survivor), 0, 2 * 20L, reference);
    });
  }

  private void handleTrapSniffing(final Game game, final Survivor player) {
    final Location origin = player.getLocation();
    final GameMap map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final java.util.Map<String, CarPart> parts = manager.getParts();
    final Collection<CarPart> stacks = parts.values();
    final Collection<org.bukkit.entity.Item> set = player.getGlowingCarParts();
    final GameProperties properties = game.getProperties();
    final double radius = properties.getPartSnifferRadius();
    for (final CarPart stack : stacks) {
      final Location location = stack.getLocation();
      final Item entity = stack.getItem();
      final double distance = origin.distanceSquared(location);
      final MetadataManager metadata = player.getMetadataManager();
      if (distance < radius * radius) {
        set.add(entity);
        metadata.setEntityGlowing(entity, NamedTextColor.RED, true);
      } else if (set.contains(entity)) {
        set.remove(entity);
        metadata.setEntityGlowing(entity, NamedTextColor.RED, false);
      }
    }
  }
}
