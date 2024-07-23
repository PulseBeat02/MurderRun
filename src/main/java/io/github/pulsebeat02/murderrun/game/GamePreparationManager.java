package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.atomic.AtomicInteger;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

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
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    final Countdown countdown = new Countdown();
    countdown.runTaskTimer(plugin, 0, 20);
  }

  public class Countdown extends BukkitRunnable {

    private final AtomicInteger secondsLeft;

    public Countdown() {
      this.secondsLeft = new AtomicInteger(121);
    }

    @Override
    public void run() {
      final int seconds = this.secondsLeft.decrementAndGet();
      switch (seconds) {
        case 5 -> {
          GamePreparationManager.this.countDownAudio();
          GamePreparationManager.this.announceCountdown(5);
        }
        case 4 -> GamePreparationManager.this.announceCountdown(4);
        case 3 -> GamePreparationManager.this.announceCountdown(3);
        case 2 -> GamePreparationManager.this.announceCountdown(2);
        case 1 -> GamePreparationManager.this.announceCountdown(1);
        case 0 -> {
          GamePreparationManager.this.futureTask();
          this.cancel();
        }
      }
    }
  }

  private void announceCountdown(final int seconds) {
    final Component title = text(seconds, RED);
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllParticipants(this.game, title, subtitle);
  }

  private void countDownAudio() {
    AdventureUtils.playSoundForAllParticipants(this.game, FXSound.COUNTDOWN);
  }

  private void futureTask() {
    this.teleportMurderers();
    this.announceReleasePhase();
    this.playReleaseSoundEffect();
  }

  private void teleportInnocentPlayers() {
    final GameSettings configuration = this.game.getSettings();
    final MurderArena arena = configuration.getArena();
    final Location spawnLocation = arena.getSpawn();
    final PlayerManager manager = this.game.getPlayerManager();
    for (final GamePlayer innocent : manager.getInnocentPlayers()) {
      final Player player = innocent.getPlayer();
      player.teleport(spawnLocation);
    }
  }

  private void teleportMurderers() {
    final GameSettings configuration = this.game.getSettings();
    final MurderArena arena = configuration.getArena();
    final Location spawnLocation = arena.getSpawn();
    final PlayerManager manager = this.game.getPlayerManager();
    for (final GamePlayer murderer : manager.getMurderers()) {
      final Player player = murderer.getPlayer();
      player.teleport(spawnLocation);
    }
  }

  private void announceHidePhase() {
    final Component title = Locale.INNOCENT_PREPERATION.build();
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllParticipants(this.game, title, subtitle);
  }

  private void announceReleasePhase() {
    final Component title = Locale.MURDERER_RELEASED.build();
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllParticipants(this.game, title, subtitle);
  }

  private void playReleaseSoundEffect() {
    AdventureUtils.playSoundForAllParticipants(this.game, FXSound.RELEASED_1, FXSound.RELEASED_2);
  }

  public MurderGame getGame() {
    return this.game;
  }
}
