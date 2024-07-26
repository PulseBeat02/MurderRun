package io.github.pulsebeat02.murderrun.game;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public final class MurderPreparationManager {

  private final MurderGame game;

  public MurderPreparationManager(final MurderGame game) {
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
    final MurderSettings configuration = this.game.getSettings();
    final MurderArena arena = configuration.getArena();
    final Location spawnLocation = arena.getSpawn();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllInnocents(innocentPlayer -> innocentPlayer.teleport(spawnLocation));
  }

  private void announceHidePhase() {
    final Component title = Locale.PREPARATION_PHASE.build();
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllParticipants(this.game, title, subtitle);
  }

  private void setBossBar() {
    final MurderSettings settings = this.game.getSettings();
    final int parts = settings.getCarPartCount();
    final Component name = Locale.BOSS_BAR.build(0, parts);
    final float progress = 0f;
    final BossBar.Color color = BossBar.Color.GREEN;
    final BossBar.Overlay overlay = BossBar.Overlay.NOTCHED_20;
    AdventureUtils.showBossBarForAllParticipants(this.game, name, progress, color, overlay);
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
    manager.applyToAllParticipants(player -> player.setXpLevel(time));
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
    this.startTimer();
  }

  private void teleportMurderers() {
    final MurderSettings configuration = this.game.getSettings();
    final MurderArena arena = configuration.getArena();
    final Location spawnLocation = arena.getSpawn();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllDead(murderer -> murderer.teleport(spawnLocation));
  }

  private void announceReleasePhase() {
    final Component title = Locale.RELEASE_PHASE.build();
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllParticipants(this.game, title, subtitle);
  }

  private void playReleaseSoundEffect() {
    AdventureUtils.playSoundForAllParticipants(this.game, FXSound.RELEASED_1, FXSound.RELEASED_2);
  }

  private void startTimer() {
    final MurderTimeManager manager = this.game.getTimeManager();
    manager.startTimer();
  }

  public MurderGame getGame() {
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
          MurderPreparationManager.this.countDownAudio();
          MurderPreparationManager.this.announceCountdown(5);
        }
        case 4 -> MurderPreparationManager.this.announceCountdown(4);
        case 3 -> MurderPreparationManager.this.announceCountdown(3);
        case 2 -> MurderPreparationManager.this.announceCountdown(2);
        case 1 -> MurderPreparationManager.this.announceCountdown(1);
        case 0 -> {
          MurderPreparationManager.this.futureTask();
          this.cancel();
        }
      }
      MurderPreparationManager.this.setTimeRemaining(seconds);
    }
  }
}
