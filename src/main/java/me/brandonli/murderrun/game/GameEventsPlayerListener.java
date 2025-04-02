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
package me.brandonli.murderrun.game;

import static java.util.Objects.requireNonNull;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.commmand.GameShutdownManager;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.capability.Capabilities;
import me.brandonli.murderrun.game.lobby.GameManager;
import me.brandonli.murderrun.game.lobby.Lobby;
import me.brandonli.murderrun.game.lobby.PreGameManager;
import me.brandonli.murderrun.game.lobby.PreGamePlayerManager;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
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

    final Map<String, PreGameManager> games = this.manager.getGames();
    final Collection<Map.Entry<@KeyFor("games") String, PreGameManager>> entries = games.entrySet();
    for (final Map.Entry<String, PreGameManager> entry : entries) {
      final PreGameManager pre = entry.getValue();
      final Game game1 = pre.getGame();
      if (game == game1) {
        manager.removeGame(pre);
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
  public void onGameStart(final Game game) {
    // Do nothing
  }
}
