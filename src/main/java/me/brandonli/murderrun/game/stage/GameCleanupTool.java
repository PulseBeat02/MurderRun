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
package me.brandonli.murderrun.game.stage;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.stream.Stream;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.*;
import me.brandonli.murderrun.game.capability.Capabilities;
import me.brandonli.murderrun.game.extension.vault.VaultManager;
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
    this.giveMoney(false);
    this.executeCommands(false);
  }

  private void handleInnocentVictory() {
    this.handleSurvivorWinStatistics();
    this.announceInnocentVictory();
    this.invalidateTimer();
    this.sprayFireworks();
    this.giveMoney(true);
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

  private void giveMoney(final boolean survivor) {
    final GameProperties properties = this.game.getProperties();
    final double money = properties.getVaultReward();
    if (Capabilities.VAULT.isDisabled()) {
      return;
    }

    if (money <= 0) {
      return;
    }

    final Game game = this.getGame();
    final MurderRun plugin = game.getPlugin();
    final VaultManager vaultManager = plugin.getVaultManager();
    final GamePlayerManager manager = this.game.getPlayerManager();
    final Stream<GamePlayer> players = survivor ? manager.getSurvivors() : manager.getKillers();
    players.forEach(player -> player.apply(raw -> vaultManager.depositMoney(raw, money)));
  }

  private void executeCommands(final boolean survivor) {
    final GameProperties properties = this.game.getProperties();
    final String chain = survivor ? properties.getSurvivorWinCommandsAfter() : properties.getKillerWinCommandsAfter();
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
