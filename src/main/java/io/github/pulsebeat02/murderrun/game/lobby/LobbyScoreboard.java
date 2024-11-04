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
package io.github.pulsebeat02.murderrun.game.lobby;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class LobbyScoreboard {

  private final PreGameManager manager;
  private final LobbySidebarManager boards;

  public LobbyScoreboard(final PreGameManager manager) {
    this.manager = manager;
    this.boards = this.createSidebar(manager);
  }

  public void addPlayer(final Player player) {
    this.boards.addPlayer(player);
  }

  public LobbySidebarManager createSidebar(@UnderInitialization LobbyScoreboard this, final PreGameManager manager) {
    final LobbySidebarManager boards = new LobbySidebarManager();
    final PreGamePlayerManager playerManager = manager.getPlayerManager();
    final Collection<Player> participants = playerManager.getParticipants();
    for (final Player player : participants) {
      boards.addPlayer(player);
    }
    return boards;
  }

  public void shutdown() {
    this.boards.delete();
  }

  public void updateScoreboard() {
    this.boards.updateTitle(this.generateTitleComponent());
    this.boards.updateLines(
        empty(),
        this.generateArenaComponent(),
        this.generatePlayerComponent(),
        empty(),
        this.generateTimerComponent(),
        empty(),
        this.generateFooterComponent()
      );
  }

  private Component generateTitleComponent() {
    return Message.LOBBY_SCOREBOARD_TITLE.build();
  }

  private Component generateFooterComponent() {
    return Message.LOBBY_SCOREBOARD_DOMAIN.build();
  }

  public Component generateTimerComponent() {
    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final LobbyTimeManager timer = playerManager.getLobbyTimeManager();
    final int time = this.getCurrentTime(timer);
    return Message.LOBBY_SCOREBOARD_TIME.build(time);
  }

  private int getCurrentTime(final @Nullable LobbyTimeManager timer) {
    if (timer == null) {
      return GameProperties.LOBBY_STARTING_TIME;
    } else {
      final LobbyTimer lobbyTimer = timer.getTimer();
      return lobbyTimer == null ? GameProperties.LOBBY_STARTING_TIME : lobbyTimer.getTime();
    }
  }

  public Component generatePlayerComponent() {
    final PreGamePlayerManager playerManager = this.manager.getPlayerManager();
    final int maxPlayers = playerManager.getMaximumPlayerCount();
    final int currentPlayers = playerManager.getCurrentPlayerCount();
    return Message.LOBBY_SCOREBOARD_PLAYERS.build(currentPlayers, maxPlayers);
  }

  private Component generateArenaComponent() {
    final GameSettings settings = this.manager.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final String name = arena.getName();
    return Message.LOBBY_SCOREBOARD_ARENA.build(name);
  }
}
