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

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.ability.AbilityManager;
import io.github.pulsebeat02.murderrun.game.extension.GameExtensionManager;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.map.GameMap;
import io.github.pulsebeat02.murderrun.game.map.MapSchematicIO;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.phase.GamePhaseInvoker;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public final class Game {

  private final MurderRun plugin;
  private final GameStatus status;

  private UUID gameID;
  private GameMap map;
  private GameSettings configuration;
  private GamePlayerManager playerManager;
  private GameTimer murderGameTimer;
  private GameScheduler scheduler;
  private GadgetManager gadgetManager;
  private AbilityManager abilityManager;
  private GameExecutor executor;
  private GamePhaseInvoker phaseInvoker;
  private GameExtensionManager extensionManager;
  private MapSchematicIO mapSchematicIO;
  private GameEventsListener callback;

  @SuppressWarnings("all") // checker
  public Game(final MurderRun plugin) {
    this.plugin = plugin;
    this.status = new GameStatus(this);
  }

  public void startGame(
    final GameSettings settings,
    final Collection<Player> murderers,
    final Collection<Player> participants,
    final GameEventsListener callback,
    final MapSchematicIO mapSchematicIO,
    final UUID uuid
  ) {
    this.gameID = uuid;
    this.status.setStatus(GameStatus.Status.SURVIVORS_RELEASED);
    this.configuration = settings;
    this.callback = callback;
    this.mapSchematicIO = mapSchematicIO;
    this.executor = new GameExecutor();
    this.scheduler = new GameScheduler(this);
    this.map = new GameMap(this);
    this.playerManager = new GamePlayerManager(this);
    this.murderGameTimer = new GameTimer();
    this.gadgetManager = new GadgetManager(this);
    this.abilityManager = new AbilityManager(this);
    this.extensionManager = new GameExtensionManager(this);
    this.phaseInvoker = new GamePhaseInvoker(this);
    this.map.start();
    this.playerManager.start(murderers, participants);
    this.extensionManager.registerExtensions();
    this.phaseInvoker.invokeStartup();
    this.gadgetManager.start();
    this.abilityManager.start();
    this.callback.onGameStart(this);
  }

  public GameSettings getSettings() {
    return this.configuration;
  }

  public void finishGame(final GameResult code) {
    final GameStatus.Status gameStatus = this.status.getStatus();
    if (gameStatus == GameStatus.Status.FINISHED || gameStatus == GameStatus.Status.NOT_STARTED) {
      return;
    }
    this.status.setStatus(GameStatus.Status.FINISHED);

    if (code == GameResult.INTERRUPTED) {
      this.forceShutdown(code);
    } else {
      final BukkitScheduler scheduler = Bukkit.getScheduler();
      scheduler.runTaskLater(this.plugin, () -> this.forceShutdown(code), 20L);
    }
  }

  private void forceShutdown(final GameResult code) {
    this.gadgetManager.shutdown();
    this.abilityManager.shutdown();
    this.scheduler.cancelAllTasks();
    this.phaseInvoker.invokeCleanup(code);
    this.executor.shutdown();
    this.extensionManager.disableExtensions();
    this.playerManager.resetAllPlayers();
    this.map.shutdown();
    this.mapSchematicIO.resetMap();
    this.callback.onGameFinish(this, code);
  }

  public GamePlayerManager getPlayerManager() {
    return this.playerManager;
  }

  public GameStatus getStatus() {
    return this.status;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public GameMap getMap() {
    return this.map;
  }

  public UUID getGameUUID() {
    return this.gameID;
  }

  public GameTimer getTimeManager() {
    return this.murderGameTimer;
  }

  public GameScheduler getScheduler() {
    return this.scheduler;
  }

  public GadgetManager getGadgetManager() {
    return this.gadgetManager;
  }

  public GameExecutor getExecutor() {
    return this.executor;
  }

  public GameExtensionManager getExtensionManager() {
    return this.extensionManager;
  }

  public MapSchematicIO getMapSchematicIO() {
    return this.mapSchematicIO;
  }

  public AbilityManager getAbilityManager() {
    return this.abilityManager;
  }
}
