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
package me.brandonli.murderrun.game.gadget.killer.utility;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class Phantom extends KillerGadget {

  public Phantom(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "phantom",
      properties.getPhantomCost(),
      ItemFactory.createGadget("phantom", properties.getPhantomMaterial(), Message.PHANTOM_NAME.build(), Message.PHANTOM_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    player.setAllowSpectatorTeleport(false);
    player.setGameMode(GameMode.SPECTATOR);

    final Location old = player.getLocation();
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    final GameProperties properties = game.getProperties();
    scheduler.scheduleTask(() -> this.setDefault(player, old), properties.getPhantomDuration(), reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getPhantomSound());

    return false;
  }

  private void setDefault(final GamePlayer player, final Location location) {
    player.setAllowSpectatorTeleport(true);
    player.teleport(location);
    player.setGameMode(GameMode.SURVIVAL);
  }
}
