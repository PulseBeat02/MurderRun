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
package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.phase.PlayerResetTool;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public final class GamePlayerLeaveEvent extends GameEvent {

  public GamePlayerLeaveEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  private void onPlayerDisconnect(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    if (!this.isGamePlayer(player)) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    gamePlayer.setLoggingOut(true);

    final String commands = GameProperties.PLAYER_LEAVE_COMMANDS_AFTER;
    if (!commands.equals("none")) {
      final String[] split = commands.split(",");
      final Server server = Bukkit.getServer();
      final ConsoleCommandSender console = server.getConsoleSender();
      for (final String command : split) {
        server.dispatchCommand(console, command);
      }
    }

    if (gamePlayer.isAlive()) {
      player.setHealth(0f);
    }

    final PlayerResetTool resetTool = new PlayerResetTool(manager);
    resetTool.handlePlayer(gamePlayer);

    final UUID uuid = gamePlayer.getUUID();
    manager.removePlayer(uuid);
  }
}
