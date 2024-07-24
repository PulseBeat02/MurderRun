package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.MurderRun;

import java.util.Collection;
import java.util.UUID;

import io.github.pulsebeat02.murderrun.map.MurderMap;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.entity.Player;

public final class MurderGame {

  private final MurderRun plugin;
  private MurderMap murderMap;
  private MurderSettings configuration;
  private PlayerManager playerManager;
  private MurderPreparationManager preparationManager;
  private MurderEndManager endManager;
  private MurderTimeManager murderTimeManager;
  private final UUID gameID;
  private MurderStatus status;

  public MurderGame(final MurderRun plugin) {
    this.plugin = plugin;
    this.status = MurderStatus.NOT_STARTED;
    this.gameID = UUID.randomUUID();
  }

  public void startGame(
      final MurderSettings settings,
      final Collection<Player> murderers,
      final Collection<Player> participants) {
    this.status = MurderStatus.IN_PROGRESS;
    this.configuration = settings;
    this.setMurdererCount(murderers);
    this.murderMap = new MurderMap(this);
    this.playerManager = new PlayerManager(this);
    this.preparationManager = new MurderPreparationManager(this);
    this.endManager = new MurderEndManager(this);
    this.murderTimeManager = new MurderTimeManager();
    this.murderMap.start();
    this.playerManager.start(murderers, participants);
    this.preparationManager.start();
  }

  private void setMurdererCount(final Collection<Player> murderers) {
    final MurderSettings settings = this.getSettings();
    final int count = murderers.size();
    settings.setMurdererCount(count);
  }

  public void finishGame(final MurderWinCode code) {
    this.status = MurderStatus.FINISHED;
    this.endManager.start(code);
    this.playerManager.shutdown();
    this.murderMap.shutdown();
  }

  public MurderSettings getSettings() {
    return this.configuration;
  }

  public PlayerManager getPlayerManager() {
    return this.playerManager;
  }

  public MurderStatus getStatus() {
    return this.status;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public MurderMap getMurderMap() {
    return this.murderMap;
  }

  public MurderPreparationManager getPreparationManager() {
    return this.preparationManager;
  }

  public MurderEndManager getEndManager() {
    return this.endManager;
  }

  public UUID getGameID() {
    return this.gameID;
  }

  public MurderTimeManager getTimeManager() {
    return this.murderTimeManager;
  }
}
