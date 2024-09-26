package io.github.pulsebeat02.murderrun.game.lobby;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.entity.Player;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class LobbyScoreboard {

  private final PreGameManager manager;
  private final Sidebar sidebar;

  public LobbyScoreboard(final PreGameManager manager) {
    this.manager = manager;
    this.sidebar = this.createSidebar(manager);
  }

  public Sidebar createSidebar(
      @UnderInitialization LobbyScoreboard this, final PreGameManager manager) {
    final MurderRun plugin = manager.getPlugin();
    final ScoreboardLibrary library = plugin.getScoreboardLibrary();
    final Sidebar sidebar = library.createSidebar();
    final PreGamePlayerManager playerManager = manager.getManager();
    final Collection<Player> participants = playerManager.getParticipants();
    for (final Player player : participants) {
      sidebar.addPlayer(player);
    }
    return sidebar;
  }

  public void initializeSidebar() {
    this.addTitle();
    this.emptyLine(0);
    this.addArena();
    this.addPlayers();
    this.emptyLine(3);
    this.addTimer();
    this.emptyLine(5);
    this.addFooter();
  }

  private void addFooter() {
    final Component msg = Message.LOBBY_SCOREBOARD_DOMAIN.build();
    this.sidebar.line(6, msg);
  }

  public void addTimer() {
    final PreGamePlayerManager playerManager = this.manager.getManager();
    final LobbyTimeManager timer = playerManager.getLobbyTimeManager();
    final int time = getCurrentTime(timer);
    final Component msg = Message.LOBBY_SCOREBOARD_TIME.build(time);
    this.sidebar.line(4, msg);
  }

  private static int getCurrentTime(final LobbyTimeManager timer) {
    if (timer == null) {
      return GameProperties.LOBBY_STARTING_TIME;
    } else {
      final LobbyTimer lobbyTimer = timer.getTimer();
      return lobbyTimer == null ? GameProperties.LOBBY_STARTING_TIME : lobbyTimer.getTime();
    }
  }

  public void addPlayers() {
    final PreGamePlayerManager playerManager = this.manager.getManager();
    final int maxPlayers = playerManager.getMaximumPlayerCount();
    final int currentPlayers = playerManager.getCurrentPlayerCount();
    final Component msg = Message.LOBBY_SCOREBOARD_PLAYERS.build(currentPlayers, maxPlayers);
    this.sidebar.line(2, msg);
  }

  private void addArena() {
    final GameSettings settings = this.manager.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final String name = arena.getName();
    final Component msg = Message.LOBBY_SCOREBOARD_ARENA.build(name);
    this.sidebar.line(1, msg);
  }

  private void addTitle() {
    final Component msg = Message.LOBBY_SCOREBOARD_TITLE.build();
    this.sidebar.title(msg);
  }

  private void emptyLine(final int index) {
    this.sidebar.line(index, empty());
  }
}
