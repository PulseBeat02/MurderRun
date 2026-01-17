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

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Killer;
import me.brandonli.murderrun.game.player.MovementManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class KillerRewind extends SurvivorGadget {

  public KillerRewind(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "killer_rewind",
        properties.getKillerRewindCost(),
        ItemFactory.createGadget(
            "killer_rewind",
            properties.getKillerRewindMaterial(),
            Message.MURDERER_REWIND_NAME.build(),
            Message.MURDERER_REWIND_LORE.build()));
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final Location location = player.getLocation();
    final GamePlayerManager manager = game.getPlayerManager();
    final MovementManager movementManager = manager.getMovementManager();
    final GamePlayer closest = manager.getNearestKiller(location);
    if (closest == null) {
      return true;
    }

    if (!(closest instanceof final Killer killer)) {
      return true;
    }

    final long current = System.currentTimeMillis();
    final long last = killer.getKillerRewindCooldown();
    final long difference = current - last;
    final GameProperties properties = game.getProperties();
    if (difference < properties.getKillerRewindCooldown()) {
      return true;
    }

    final boolean successful = movementManager.handleRewind(killer);
    if (!successful) {
      return true;
    }
    item.remove();

    killer.setKillerRewindCooldown(current);
    killer.setFallDistance(0.0f);

    final Component msg = Message.REWIND_ACTIVATE.build();
    final PlayerAudience audience = killer.getAudience();
    audience.sendMessage(msg);
    audience.playSound(Sounds.REWIND);

    return false;
  }
}
