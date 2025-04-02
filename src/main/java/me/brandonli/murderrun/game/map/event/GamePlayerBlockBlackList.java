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
package me.brandonli.murderrun.game.map.event;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Killer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public final class GamePlayerBlockBlackList extends GameEvent {

  public GamePlayerBlockBlackList(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(final BlockBreakEvent event) {
    final Player player = event.getPlayer();
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (!(gamePlayer instanceof Killer)) {
      return;
    }

    final GameStatus status = game.getStatus();
    final GameStatus.Status gameStatus = status.getStatus();
    if (gameStatus == GameStatus.Status.SURVIVORS_RELEASED) {
      event.setCancelled(true);
    }
  }
}
