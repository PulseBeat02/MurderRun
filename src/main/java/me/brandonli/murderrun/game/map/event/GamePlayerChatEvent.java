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

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class GamePlayerChatEvent extends GameEvent {

  public GamePlayerChatEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerChat(final AsyncChatEvent event) {
    final Player player = event.getPlayer();
    if (!this.isGamePlayer(player)) {
      return;
    }
    event.setCancelled(true);

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final Component component = event.message();
    final ChatRenderer renderer = event.renderer();
    final Component displayComponent = player.displayName();
    final Server server = Bukkit.getServer();
    final Component msg = renderer.render(player, displayComponent, component, server);
    if (gamePlayer.isAlive()) {
      manager.sendMessageToAllParticipants(msg);
      return;
    }

    final Component deadMsg = Message.DEAD_CHAT_PREFIX.build(msg);
    manager.sendMessageToAllDeceased(deadMsg);
  }
}
