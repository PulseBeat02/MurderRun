package io.github.pulsebeat02.murderrun.game.stage;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameResult;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.GameTimer;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.statistics.PlayerStatistics;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Optional;

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
    this.sprayFireworks();
  }

  private void handleInnocentVictory() {
    this.handleSurvivorWinStatistics();
    this.announceInnocentVictory();
    this.invalidateTimer();
    this.sprayFireworks();
  }

  private void stopTimer() {
    final GameTimer manager = this.game.getTimeManager();
    manager.stopTimer();
  }

  private Component generateWinnerMessage(final boolean innocents) {
    final Component separator = Message.WINNER_SEPARATOR.build();
    return separator
      .appendNewline()
      .append(this.getWinComponent(innocents))
      .appendNewline()
      .append(this.getKillComponent())
      .appendNewline()
      .append(this.getPartComponent())
      .appendNewline()
      .append(separator);
  }

  private Component getPartComponent() {
    final PlayerManager manager = this.game.getPlayerManager();
    final Optional<Survivor> optional = manager.getSurvivorWithMostCarPartsRetrieved();
    if (optional.isEmpty()) {
      return Message.WINNER_PARTS.build("?", 0);
    }

    final Survivor survivor = optional.get();
    final int carParts = survivor.getCarPartsRetrieved();
    final String survivorName = survivor.getDisplayName();
    return Message.WINNER_PARTS.build(survivorName, carParts);
  }

  private Component getKillComponent() {

    final PlayerManager manager = this.game.getPlayerManager();
    final Optional<Killer> optional = manager.getKillerWithMostKills();
    if (optional.isEmpty()) {
      return Message.WINNER_KILLS.build("?", 0);
    }

    final Killer killer = optional.get();
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
    final Component title = Message.GAME_WINNER_TITLE.build();
    final Component subtitle = Message.GAME_WINNER_TITLE_SURVIVOR.build();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.sendMessageToAllParticipants(winner);
    manager.playSoundForAllInnocents(Sounds.WIN);
    manager.playSoundForAllMurderers(Sounds.LOSS);
    manager.showTitleForAllParticipants(title, subtitle);
  }

  private void invalidateTimer() {
    final GameTimer manager = this.game.getTimeManager();
    manager.invalidateElapsedTime();
  }

  private void announceMurdererVictory() {
    final Component winner = this.generateWinnerMessage(false);
    final Component title = Message.GAME_WINNER_TITLE.build();
    final Component subtitle = Message.GAME_WINNER_TITLE_KILLER.build();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.sendMessageToAllParticipants(winner);
    manager.playSoundForAllInnocents(Sounds.LOSS);
    manager.playSoundForAllMurderers(Sounds.WIN);
    manager.showTitleForAllParticipants(title, subtitle);
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

  private void sprayFireworks() {
    final GameSettings settings = this.game.getSettings();
    final Lobby lobby = requireNonNull(settings.getLobby());
    final Location spawn = lobby.getLobbySpawn();
    final World world = requireNonNull(spawn.getWorld());
    for (int i = 0; i < 10; i++) {
      final Firework firework = world.spawn(spawn, Firework.class);
      final FireworkMeta meta = firework.getFireworkMeta();
      final FireworkEffect effect = FireworkEffect.builder()
        .withColor(Color.fromRGB(255, 0, 0))
        .withFade(Color.fromRGB(0, 255, 0))
        .with(FireworkEffect.Type.BALL)
        .withTrail()
        .withFlicker()
        .build();
      meta.addEffect(effect);
      meta.setPower(2);
      firework.setFireworkMeta(meta);
    }
  }

  public Game getGame() {
    return this.game;
  }
}
