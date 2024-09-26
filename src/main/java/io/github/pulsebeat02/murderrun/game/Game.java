package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.citizens.CitizensManager;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.libsdiguises.DisguiseManager;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.stage.GameCleanupTool;
import io.github.pulsebeat02.murderrun.game.stage.GameStartupTool;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.entity.Player;

public final class Game {

  private final MurderRun plugin;
  private final UUID gameID;

  private Map map;
  private GameSettings configuration;
  private PlayerManager playerManager;
  private GameStartupTool preparationManager;
  private GameCleanupTool cleanupManager;
  private GameTimer murderGameTimer;
  private GameScheduler scheduler;
  private GameStatus status;
  private GadgetManager gadgetManager;
  private GameExecutor executor;
  private CitizensManager npcManager;
  private GameEventsListener callback;
  private DisguiseManager disguiseManager;

  public Game(final MurderRun plugin) {
    this.plugin = plugin;
    this.status = GameStatus.NOT_STARTED;
    this.gameID = UUID.randomUUID();
  }

  public GameSettings getConfiguration() {
    return this.configuration;
  }

  public void startGame(
      final GameSettings settings,
      final Collection<Player> murderers,
      final Collection<Player> participants,
      final GameEventsListener callback) {
    this.status = GameStatus.SURVIVORS_RELEASED;
    this.configuration = settings;
    this.callback = callback;
    this.executor = new GameExecutor();
    this.scheduler = new GameScheduler(this);
    this.map = new Map(this);
    this.playerManager = new PlayerManager(this);
    this.preparationManager = new GameStartupTool(this);
    this.cleanupManager = new GameCleanupTool(this);
    this.murderGameTimer = new GameTimer();
    this.gadgetManager = new GadgetManager(this);
    this.npcManager = new CitizensManager(this);
    this.map.start();
    this.gadgetManager.start();
    this.playerManager.start(murderers, participants);
    this.preparationManager.start();
    this.registerExtensions();
    this.callback.onGameStart(this);
  }

  private void registerExtensions() {
    if (Capabilities.LIB_DISGUISES.isEnabled()) {
      this.disguiseManager = new DisguiseManager();
    }
  }

  public GameSettings getSettings() {
    return this.configuration;
  }

  public void finishGame(final GameResult code) {
    if (this.status != GameStatus.NOT_STARTED) {
      this.status = GameStatus.FINISHED;
      this.gadgetManager.shutdown();
      this.scheduler.cancelAllTasks();
      this.npcManager.shutdown();
      this.playerManager.resetAllPlayers();
      this.cleanupManager.start(code);
      this.map.shutdown();
      this.executor.shutdown();
      this.callback.onGameFinish(this, code);
      this.disableDisguiseHandler();
    }
  }

  private void disableDisguiseHandler() {
    if (Capabilities.LIB_DISGUISES.isDisabled()) {
      return;
    }
    this.disguiseManager.shutdown();
  }

  public PlayerManager getPlayerManager() {
    return this.playerManager;
  }

  public GameStatus getStatus() {
    return this.status;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public Map getMap() {
    return this.map;
  }

  public GameStartupTool getPreparationManager() {
    return this.preparationManager;
  }

  public GameCleanupTool getCleanupManager() {
    return this.cleanupManager;
  }

  public UUID getGameUUID() {
    return this.gameID;
  }

  public GameTimer getTimeManager() {
    return this.murderGameTimer;
  }

  public boolean isFinished() {
    return this.status == GameStatus.FINISHED;
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

  public CitizensManager getNPCManager() {
    return this.npcManager;
  }

  public void setStatus(final GameStatus status) {
    this.status = status;
  }

  public DisguiseManager getDisguiseManager() {
    return this.disguiseManager;
  }
}
