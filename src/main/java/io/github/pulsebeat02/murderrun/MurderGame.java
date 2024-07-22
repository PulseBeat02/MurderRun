package io.github.pulsebeat02.murderrun;

import io.github.pulsebeat02.murderrun.config.GameConfiguration;
import java.util.List;

import io.github.pulsebeat02.murderrun.map.MurderMap;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public final class MurderGame {

  private final MurderRun plugin;
  private final MurderMap map;
  private GameConfiguration configuration;
  private PlayerManager playerManager;
  private GameStatus status;

  public MurderGame(final MurderRun plugin) {
    this.plugin = plugin;
    this.map = new MurderMap(this);
    this.status = GameStatus.NOT_STARTED;
  }

  public void startGame(final List<Player> participants) {
    this.status = GameStatus.IN_PROGRESS;
    this.configuration = new GameConfiguration();
    this.playerManager = new PlayerManager(this.configuration, participants);
    this.map.start();
  }

  public void finishGame() {
    this.status = GameStatus.FINISHED;
    this.map.stop();
    this.playerManager.resetAllPlayers();
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
