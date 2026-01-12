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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Item;

public final class Miniaturizer extends SurvivorGadget {

  public Miniaturizer(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "miniaturizer",
      properties.getMiniaturizerCost(),
      ItemFactory.createGadget(
        "miniaturizer",
        properties.getMiniturizerMaterial(),
        Message.MINIATURIZER_NAME.build(),
        Message.MINIATURIZER_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GameScheduler scheduler = game.getScheduler();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    final GameProperties properties = game.getProperties();
    final int duration = properties.getMiniaturizerDuration();
    final StrictPlayerReference ref = StrictPlayerReference.of(player);
    final double scale = properties.getMiniaturizerScale();
    final AttributeInstance instance = requireNonNull(player.getAttribute(Attribute.SCALE));
    instance.setBaseValue(scale);
    scheduler.scheduleTask(() -> player.resetAttribute(Attribute.SCALE), duration, ref);
    item.remove();
    return false;
  }
}
