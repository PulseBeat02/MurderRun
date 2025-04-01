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
package io.github.pulsebeat02.murderrun.game;

import static java.util.Objects.requireNonNull;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.commmand.GameShutdownManager;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.capability.Capabilities;
import io.github.pulsebeat02.murderrun.game.lobby.GameManager;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.PreGameManager;
import io.github.pulsebeat02.murderrun.game.lobby.PreGamePlayerManager;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.KeyFor;

public final class GameEventsPlayerListener implements GameEventsListener {

  private final GameManager manager;

  public GameEventsPlayerListener(final GameManager manager) {
    this.manager = manager;
  }

  @Override
  public void onGameFinish(final Game game, final GameResult result) {
    final MurderRun plugin = this.manager.getPlugin();
    final GameShutdownManager manager = plugin.getGameShutdownManager();
    manager.removeGame(game);

    final Map<String, PreGameManager> games = this.manager.getGames();
    final Collection<Map.Entry<@KeyFor("games") String, PreGameManager>> entries = games.entrySet();
    for (final Map.Entry<String, PreGameManager> entry : entries) {
      final PreGameManager pre = entry.getValue();
      final Game game1 = pre.getGame();
      if (game == game1) {
        final String id = entry.getKey();
        games.remove(id);
        this.sendRequeueMessage(pre);
        break;
      }
    }
  }

  private void sendRequeueMessage(final PreGameManager manager) {
    if (Capabilities.PARTIES.isDisabled()) {
      return;
    }

    final PreGamePlayerManager playerManager = manager.getPlayerManager();
    final CommandSender sender = playerManager.getLeader();
    if (!(sender instanceof final Player player)) {
      return;
    }

    final UUID uuid = player.getUniqueId();
    final PartiesAPI api = Parties.getApi();
    final Party party = api.getPartyOfPlayer(uuid);
    if (party == null) {
      return;
    }

    final GameSettings settings = manager.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Lobby lobby = requireNonNull(settings.getLobby());
    final String arenaName = arena.getName();
    final String lobbyName = lobby.getName();
    final Component msg = Message.GAME_REQUEUE.build(arenaName, lobbyName);
    final MurderRun plugin = manager.getPlugin();
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final Audience audience = audiences.player(uuid);
    audience.sendMessage(msg);
  }

  @Override
  public void onGameStart(final Game game) {}
}
