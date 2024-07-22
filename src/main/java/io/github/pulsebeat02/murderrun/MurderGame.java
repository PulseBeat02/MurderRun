package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.config.GameConfiguration;

import java.util.Collection;

import io.github.pulsebeat02.murderrun.map.MurderMap;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.entity.Player;

public final class MurderGame {

  private final MurderRun plugin;
  private final MurderMap murderMap;
  private final GameConfiguration configuration;
  private final PlayerManager playerManager;
  private GameStatus status;

  public MurderGame(final MurderRun plugin) {
    this.plugin = plugin;
    this.murderMap = new MurderMap(this);
    this.playerManager = new PlayerManager(this);
    this.configuration = new GameConfiguration();
    this.status = GameStatus.NOT_STARTED;
  }

  public void startGame(final Collection<Player> murderers, final Collection<Player> participants) {
    this.status = GameStatus.IN_PROGRESS;
    this.murderMap.start();
    this.playerManager.start(murderers, participants);
  }

  public void finishGame() {
    this.status = GameStatus.FINISHED;
    this.murderMap.shutdown();
    this.playerManager.shutdown();
  }

  public GameConfiguration getConfiguration() {
    return this.configuration;
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
}
