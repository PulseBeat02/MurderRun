package io.github.pulsebeat02.murderrun.game.stage;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameResult;
import io.github.pulsebeat02.murderrun.game.GameTimer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.statistics.PlayerStatistics;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import net.kyori.adventure.text.Component;

public final class GameCleanupTool {

  private final Game game;

  public GameCleanupTool(final Game game) {
    this.game = game;
  }

  public void start(final GameResult winCode) {
    this.initiateEndingSequence(winCode);
  }

  private void initiateEndingSequence(final GameResult winCode) {
    this.stopTimer();
    switch (winCode) {
      case INNOCENTS -> this.handleInnocentVictory();
      case MURDERERS -> this.handleKillerVictory();
      default -> {} // do nothing
    }
  }

  private void handleKillerVictory() {
    this.handleKillerWinStatistics();
    this.announceMurdererVictory();
    this.announceMurdererTime();
  }

  private void handleInnocentVictory() {
    this.handleSurvivorWinStatistics();
    this.announceInnocentVictory();
    this.invalidateTimer();
  }

  private void stopTimer() {
    final GameTimer manager = this.game.getTimeManager();
    manager.stopTimer();
  }

  private Component generateWinnerMessage(final boolean innocents) {
    final Component separator = Message.WINNER_SEPARATOR.build();
    return separator.appendNewline()
            .append(this.getWinComponent(innocents)).appendNewline()
            .append(this.getKillComponent()).appendNewline()
            .append(this.getPartComponent()).appendNewline()
            .append(separator);
  }

  private Component getPartComponent() {
    final PlayerManager manager = this.game.getPlayerManager();
    final Survivor survivor = manager.getSurvivorWithMostCarPartsRetrieved();
    final int carParts = survivor.getCarPartsRetrieved();
    final String survivorName = survivor.getDisplayName();
    return Message.WINNER_PARTS.build(survivorName, carParts);
  }

  private Component getKillComponent() {
    final PlayerManager manager = this.game.getPlayerManager();
    final Killer killer = manager.getKillerWithMostKills();
    final int count = killer.getKills();
    final String name = killer.getDisplayName();
    return Message.WINNER_KILLS.build(name, count);
  }

  private Component getWinComponent(final boolean innocents) {
    final GameTimer timer = this.game.getTimeManager();
    final long seconds = timer.getElapsedTime();
    return innocents ? Message.WINNER_SURVIVOR.build(seconds) : Message.WINNER_KILLER.build(seconds);
  }

  private void announceInnocentVictory() {
    final Component winner = this.generateWinnerMessage(true);
    final PlayerManager manager = this.game.getPlayerManager();
    manager.sendMessageToAllParticipants(winner);
    manager.playSoundForAllInnocents(Sounds.WIN);
    manager.playSoundForAllMurderers(Sounds.LOSS);
  }

  private void invalidateTimer() {
    final GameTimer manager = this.game.getTimeManager();
    manager.invalidateElapsedTime();
  }

  private void announceMurdererVictory() {
    final Component winner = this.generateWinnerMessage(false);
    final PlayerManager manager = this.game.getPlayerManager();
    manager.sendMessageToAllParticipants(winner);
    manager.playSoundForAllInnocents(Sounds.LOSS);
    manager.playSoundForAllMurderers(Sounds.WIN);
  }

  private void announceMurdererTime() {
    final GameTimer timer = this.game.getTimeManager();
    final long timeElapsed = timer.getElapsedTime();
    final Component message = Message.FINAL_TIME.build(timeElapsed);
    final PlayerManager manager = this.game.getPlayerManager();
    manager.sendMessageToAllParticipants(message);
  }

  private void saveData() {
    final MurderRun plugin = this.game.getPlugin();
    plugin.updatePluginData();
  }

  private void handleSurvivorWinStatistics() {
    final GameTimer timer = this.game.getTimeManager();
    final long timeElapsed = timer.getElapsedTime();
    final MurderRun plugin = this.game.getPlugin();
    final StatisticsManager statistics = plugin.getStatisticsManager();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllParticipants(participant -> {
      final PlayerStatistics stats = statistics.getOrCreatePlayerStatistic(participant);
      if (participant instanceof Survivor) {
        stats.insertFastestWinSurvivor(timeElapsed);
        stats.incrementTotalWins();
      } else {
        stats.incrementTotalLosses();
      }
    });
    this.saveData();
  }

  private void handleKillerWinStatistics() {
    final GameTimer timer = this.game.getTimeManager();
    final long timeElapsed = timer.getElapsedTime();
    final MurderRun plugin = this.game.getPlugin();
    final StatisticsManager statistics = plugin.getStatisticsManager();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllParticipants(participant -> {
      final PlayerStatistics stats = statistics.getOrCreatePlayerStatistic(participant);
      if (participant instanceof Killer) {
        stats.insertFastestWinSurvivor(timeElapsed);
        stats.incrementTotalWins();
      } else {
        stats.incrementTotalLosses();
      }
    });
    this.saveData();
  }

  public Game getGame() {
    return this.game;
  }
}
