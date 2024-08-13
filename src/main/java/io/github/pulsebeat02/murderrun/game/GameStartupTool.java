package io.github.pulsebeat02.murderrun.game;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundKeys;
import io.github.pulsebeat02.murderrun.utils.ComponentUtils;
import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public final class GameStartupTool {

  private final Game game;

  public GameStartupTool(final Game game) {
    this.game = game;
  }

  public void start() {
    this.teleportInnocentPlayers();
    this.announceHidePhase();
    this.setBossBar();
    this.clearNetherStars();
    this.runFutureTask();
  }

  private void teleportInnocentPlayers() {
    final GameSettings configuration = this.game.getSettings();
    final Arena arena = requireNonNull(configuration.getArena());
    final Location spawnLocation = arena.getSpawn();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllInnocents(innocentPlayer -> innocentPlayer.teleport(spawnLocation));
  }

  private void announceHidePhase() {
    final Component title = Locale.PREPARATION_PHASE.build();
    final Component subtitle = empty();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.showTitleForAllParticipants(title, subtitle);
  }

  private void setBossBar() {
    final GameSettings settings = this.game.getSettings();
    final int parts = settings.getCarPartCount();
    final Component name = Locale.BOSS_BAR.build(0, parts);
    final float progress = 0f;
    final BossBar.Color color = BossBar.Color.GREEN;
    final BossBar.Overlay overlay = BossBar.Overlay.NOTCHED_20;
    ComponentUtils.showBossBarForAllParticipants(this.game, name, progress, color, overlay);
  }

  private void clearNetherStars() {
    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllParticipants(player -> player.getInventory().remove(Material.NETHER_STAR));
  }

  private void runFutureTask() {
    final MurderRun plugin = this.game.getPlugin();
    final Countdown countdown = new Countdown();
    countdown.runTaskTimer(plugin, 0, 20);
  }

  private void setTimeRemaining(final int time) {
    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllParticipants(gamePlayer -> gamePlayer.apply(player -> player.setLevel(time)));
  }

  private void announceCountdown(final int seconds) {
    final Component title = text(seconds, RED);
    final Component subtitle = empty();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.showTitleForAllParticipants(title, subtitle);
  }

  private void countDownAudio() {
    ComponentUtils.playSoundForAllParticipants(this.game, SoundKeys.COUNTDOWN);
  }

  private void futureTask() {
    this.teleportMurderers();
    this.announceReleasePhase();
    this.playReleaseSoundEffect();
    this.startTimer();
  }

  private void teleportMurderers() {
    final GameSettings configuration = this.game.getSettings();
    final Arena arena = requireNonNull(configuration.getArena());
    final Location spawnLocation = arena.getSpawn();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllDead(murderer -> murderer.teleport(spawnLocation));
  }

  private void announceReleasePhase() {
    final Component title = Locale.RELEASE_PHASE.build();
    final Component subtitle = empty();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.showTitleForAllParticipants(title, subtitle);
  }

  private void playReleaseSoundEffect() {
    ComponentUtils.playSoundForAllParticipants(
        this.game, SoundKeys.RELEASED_1, SoundKeys.RELEASED_2);
  }

  private void startTimer() {
    final GameTimer manager = this.game.getTimeManager();
    manager.startTimer();
  }

  public Game getGame() {
    return this.game;
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
          GameStartupTool.this.countDownAudio();
          GameStartupTool.this.announceCountdown(5);
        }
        case 4 -> GameStartupTool.this.announceCountdown(4);
        case 3 -> GameStartupTool.this.announceCountdown(3);
        case 2 -> GameStartupTool.this.announceCountdown(2);
        case 1 -> GameStartupTool.this.announceCountdown(1);
        case 0 -> {
          GameStartupTool.this.futureTask();
          this.cancel();
        }
      }
      GameStartupTool.this.setTimeRemaining(seconds);
    }
  }
}
