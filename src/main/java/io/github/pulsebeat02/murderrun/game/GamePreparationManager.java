package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.config.GameConfiguration;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.title.Title.title;

public final class GamePreparationManager {

  private final MurderGame game;

  public GamePreparationManager(final MurderGame game) {
    this.game = game;
  }

  public void start() {
    this.teleportInnocentPlayers();
    this.announceHidePhase();
    this.runFutureTask();
  }

  private void runFutureTask() {
    final MurderRun plugin = this.game.getPlugin();
    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::futureTask, 2 * 60 * 20);
  }

  private void futureTask() {
    this.teleportMurderers();
    this.announceReleasePhase();
  }

  private void teleportInnocentPlayers() {
    final GameConfiguration configuration = this.game.getConfiguration();
    final Location spawnLocation = configuration.getMapSpawn();
    final PlayerManager manager = this.game.getPlayerManager();
    for (final GamePlayer innocent : manager.getInnocentPlayers()) {
      final Player player = innocent.getPlayer();
      player.teleport(spawnLocation);
    }
  }

  private void teleportMurderers() {
    final GameConfiguration configuration = this.game.getConfiguration();
    final Location spawnLocation = configuration.getMapSpawn();
    final PlayerManager manager = this.game.getPlayerManager();
    for (final GamePlayer innocent : manager.getMurderers()) {
      final Player player = innocent.getPlayer();
      player.teleport(spawnLocation);
    }
  }

  private void announceHidePhase() {
    final PlayerManager manager = this.game.getPlayerManager();
    for (final GamePlayer gamePlayer : manager.getParticipants()) {
      final Player player = gamePlayer.getPlayer();
      player.showTitle(title(Locale.INNOCENT_PREPERATION.build(), empty()));
    }
  }

  private void announceReleasePhase() {
    final PlayerManager manager = this.game.getPlayerManager();
    for (final GamePlayer gamePlayer : manager.getParticipants()) {
      final Player player = gamePlayer.getPlayer();
      player.showTitle(title(Locale.MURDERER_RELEASED.build(), empty()));
    }
  }
}
