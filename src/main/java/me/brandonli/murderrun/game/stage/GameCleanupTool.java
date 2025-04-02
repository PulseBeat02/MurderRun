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
package me.brandonli.murderrun.game.stage;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.stream.Stream;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.*;
import me.brandonli.murderrun.game.lobby.Lobby;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Killer;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.statistics.PlayerStatistics;
import me.brandonli.murderrun.game.statistics.StatisticsManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

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
    this.executeCommands(false);
  }

  private void handleInnocentVictory() {
    this.handleSurvivorWinStatistics();
    this.announceInnocentVictory();
    this.invalidateTimer();
    this.sprayFireworks();
    this.executeCommands(true);
  }

  private void stopTimer() {
    final GameTimer manager = this.game.getTimeManager();
    manager.stopTimer();
  }

  private Component generateWinnerMessage(final boolean innocents) {
    final Component separator = Message.WINNER_SEPARATOR.build();
    final Component win = innocents ? this.getSurvivorWinComponent() : this.getKillerWinComponent();
    return separator
      .appendNewline()
      .append(win)
      .appendNewline()
      .append(this.getKillComponent())
      .appendNewline()
      .append(this.getPartComponent())
      .appendNewline()
      .append(separator);
  }

  private Component getPartComponent() {
    final GamePlayerManager manager = this.game.getPlayerManager();
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
    final GamePlayerManager manager = this.game.getPlayerManager();
    final Optional<Killer> optional = manager.getKillerWithMostKills();
    if (optional.isEmpty()) {
      return Message.WINNER_KILLS.build("?", 0);
    }

    final Killer killer = optional.get();
    final int count = killer.getKills();
    final String name = killer.getDisplayName();
    return Message.WINNER_KILLS.build(name, count);
  }

  private Component getSurvivorWinComponent() {
    final GameTimer timer = this.game.getTimeManager();
    final long seconds = timer.getElapsedTime();
    return Message.WINNER_SURVIVOR.build(seconds);
  }

  private Component getKillerWinComponent() {
    final GameTimer timer = this.game.getTimeManager();
    final long seconds = timer.getElapsedTime();
    return Message.WINNER_KILLER.build(seconds);
  }

  private void announceInnocentVictory() {
    final Component winner = this.generateWinnerMessage(true);
    final Component title = Message.GAME_WINNER_TITLE.build();
    final Component subtitle = Message.GAME_WINNER_TITLE_SURVIVOR.build();
    final GamePlayerManager manager = this.game.getPlayerManager();
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
    final GamePlayerManager manager = this.game.getPlayerManager();
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
    final GamePlayerManager manager = this.game.getPlayerManager();
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
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllParticipants(participant -> {
      final PlayerStatistics stats = statistics.getOrCreatePlayerStatistic(participant);
      if (participant instanceof Killer) {
        stats.insertFastestWinKiller(timeElapsed);
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

  private void executeCommands(final boolean survivor) {
    final String chain = survivor ? GameProperties.SURVIVOR_WIN_COMMANDS_AFTER : GameProperties.KILLER_WIN_COMMANDS_AFTER;
    if (chain.equalsIgnoreCase("none")) {
      return;
    }

    final GamePlayerManager manager = this.game.getPlayerManager();
    final Stream<GamePlayer> players = survivor ? manager.getSurvivors() : manager.getKillers();
    final String[] commands = chain.split(";");
    players.forEach(player -> this.runCommands(player, commands));
  }

  private void runCommands(final GamePlayer player, final String[] commands) {
    final Server server = Bukkit.getServer();
    final ConsoleCommandSender console = server.getConsoleSender();
    final String name = player.getName();
    for (final String command : commands) {
      final String replaced = command.replace("<player>", name);
      server.dispatchCommand(console, replaced);
    }
  }

  public Game getGame() {
    return this.game;
  }
}
