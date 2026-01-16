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
package me.brandonli.murderrun.game.lobby;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import me.brandonli.murderrun.game.GameMode;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.locale.Message;
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
        this.generateDateComponent(),
        empty(),
        this.generateArenaComponent(),
        this.generatePlayerComponent(),
        empty(),
        this.generateTimerComponent(),
        empty(),
        this.generateModeComponent(),
        empty(),
        this.generateFooterComponent()
      );
  }

  private Component generateDateComponent() {
    final ZoneId zoneId = ZoneId.systemDefault();
    final LocalDate now = LocalDate.now(zoneId);
    final int day = now.getDayOfMonth();
    final int month = now.getMonthValue();
    final int year = now.getYear();
    return Message.LOBBY_SCOREBOARD_DATE.build(day, month, year);
  }

  private Component generateModeComponent() {
    final GameMode mode = this.manager.getMode();
    final String name = mode.getModeName();
    return Message.LOBBY_SCOREBOARD_MODE.build(name);
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
    final GameProperties properties = this.manager.getProperties();
    final int time = properties.getLobbyStartingTime();
    if (timer == null) {
      return time;
    } else {
      final LobbyTimer lobbyTimer = timer.getTimer();
      return lobbyTimer == null ? time : lobbyTimer.getTime();
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
