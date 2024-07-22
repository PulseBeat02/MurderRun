package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.config.GameConfiguration;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.SplittableRandom;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.title.Title.title;

public final class GamePreparationManager {

  private static final SplittableRandom RANDOM = new SplittableRandom();

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
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.scheduleSyncDelayedTask(plugin, this::countDownAudio, 114 * 20);
    scheduler.scheduleSyncDelayedTask(plugin, this::futureTask, 120 * 20);
  }

  private void countDownAudio() {
    final String key = "murder_run:countdown";
    final PlayerManager manager = this.game.getPlayerManager();
    for (final GamePlayer gamePlayer : manager.getParticipants()) {
      final Player player = gamePlayer.getPlayer();
      final Location location = player.getLocation();
      player.playSound(location, key, SoundCategory.MASTER, 1, 1);
    }
  }

  private void futureTask() {
    this.teleportMurderers();
    this.announceReleasePhase();
    this.playReleaseSoundEffect();
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

  private void playReleaseSoundEffect() {
    final String[] sounds = new String[] {"murder_run:released_1", "murder_run:released_2"};
    final int index = this.generateRandomIndex(0, 2);
    final String key = sounds[index];
    final PlayerManager manager = this.game.getPlayerManager();
    for (final GamePlayer gamePlayer : manager.getParticipants()) {
      final Player player = gamePlayer.getPlayer();
      final Location location = player.getLocation();
      player.playSound(location, key, SoundCategory.AMBIENT, 1, 1);
    }
  }

  private int generateRandomIndex(final int min, final int max) {
    return RANDOM.nextInt(min, max);
  }
}
