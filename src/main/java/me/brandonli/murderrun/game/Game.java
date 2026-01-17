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

import java.util.Collection;
import java.util.UUID;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.ability.AbilityManager;
import me.brandonli.murderrun.game.extension.GameExtensionManager;
import me.brandonli.murderrun.game.freezetag.FreezeTagManager;
import me.brandonli.murderrun.game.gadget.GadgetManager;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.map.MapSchematicIO;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.phase.GamePhaseInvoker;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public final class Game {

  private final MurderRun plugin;
  private final GameStatus status;

  private UUID gameID;
  private GameMode mode;
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
  private GameProperties properties;
  private FreezeTagManager freezeTagManager;

  @SuppressWarnings("all") // checker
  public Game(final MurderRun plugin) {
    this.plugin = plugin;
    this.status = new GameStatus(this);
  }

  public Game(final MurderRun plugin, final GameProperties properties) {
    this.properties = properties;
    this(plugin);
  }

  public void startGame(
      final GameProperties properties,
      final GameMode mode,
      final GameSettings settings,
      final Collection<Player> murderers,
      final Collection<Player> participants,
      final GameEventsListener callback,
      final MapSchematicIO mapSchematicIO,
      final UUID uuid) {
    this.gameID = uuid;
    this.mode = mode;
    this.status.setStatus(GameStatus.Status.SURVIVORS_RELEASED);
    this.configuration = settings;
    this.callback = callback;
    this.mapSchematicIO = mapSchematicIO;
    this.properties = properties;
    this.executor = new GameExecutor();
    this.scheduler = new GameScheduler(this);
    this.map = new GameMap(this);
    this.playerManager = new GamePlayerManager(this);
    this.murderGameTimer = new GameTimer();
    this.gadgetManager = new GadgetManager(this);
    this.abilityManager = new AbilityManager(this);
    this.extensionManager = new GameExtensionManager(this);
    this.phaseInvoker = new GamePhaseInvoker(this);
    if (mode == GameMode.FREEZE_TAG) {
      this.freezeTagManager = new FreezeTagManager(this);
    }

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
      scheduler.runTaskLater(this.plugin, () -> this.forceShutdown(code), 5L);
    }
  }

  private void forceShutdown(final GameResult code) {
    this.gadgetManager.shutdown();
    this.abilityManager.shutdown();
    this.scheduler.cancelAllTasks();
    this.phaseInvoker.invokeCleanup(code);
    this.executor.shutdown();
    this.extensionManager.disableExtensions();
    if (this.freezeTagManager != null) {
      this.freezeTagManager.shutdown();
    }
    this.playerManager.shutdown();
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

  public GameProperties getProperties() {
    return this.properties;
  }

  public FreezeTagManager getFreezeTagManager() {
    return this.freezeTagManager;
  }

  public GameMode getMode() {
    return this.mode;
  }
}
