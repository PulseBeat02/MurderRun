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

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import me.brandonli.murderrun.utils.map.MapUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

public final class EagleEye extends KillerGadget {

  public EagleEye() {
    super(
      "eagle_eye",
      GameProperties.EAGLE_EYE_COST,
      ItemFactory.createGadget(
        "eagle_eye",
        GameProperties.EAGLE_EYE_MATERIAL,
        Message.EAGLE_EYE_NAME.build(),
        Message.EAGLE_EYE_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location[] corners = arena.getCorners();
    final Location average = MapUtils.getAverageLocation(corners[0], corners[1]);
    final World world = requireNonNull(average.getWorld());

    final Block highest = world.getHighestBlockAt(average);
    final Location location = highest.getLocation();
    final Location teleport = location.add(0, 50, 0);

    final Location previous = player.getLocation();
    player.setGravity(false);
    player.teleport(teleport);
    player.setAllowFlight(true);

    final float before = player.getFlySpeed();
    player.setFlySpeed(0.0f);

    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    scheduler.scheduleTask(() -> this.resetState(player, previous, before), GameProperties.EAGLE_EYE_DURATION, reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.EAGLE_EYE_SOUND);

    return false;
  }

  private void resetState(final GamePlayer gamePlayer, final Location previous, final float flySpeed) {
    gamePlayer.teleport(previous);
    gamePlayer.setGravity(true);
    gamePlayer.setAllowFlight(false);
    gamePlayer.setFlySpeed(flySpeed);
  }
}
