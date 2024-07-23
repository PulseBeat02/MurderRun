package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitScheduler;

import static net.kyori.adventure.text.Component.empty;

public final class GameEndManager {

  private final MurderGame game;

  public GameEndManager(final MurderGame game) {
    this.game = game;
  }

  public void start(final GameWinCode winCode) {
    final MurderRun plugin = this.game.getPlugin();
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.scheduleSyncDelayedTask(plugin, () -> this.initiateEndingSequence(winCode), 2 * 20);
  }

  private void initiateEndingSequence(final GameWinCode winCode) {
    this.stopTimer();
    switch (winCode) {
      case INNOCENTS -> {
        this.announceInnocentVictory();
        this.invalidateTimer();
      }
      case MURDERERS -> this.announceMurdererVictory();
    }
    this.announceMurdererTime();
  }

  private void announceMurdererTime() {
    final TimeManager manager = this.game.getTimeManager();
    final long timeElapsed = manager.getElapsedTime();
    final Component message = Locale.MURDERER_TIME.build(timeElapsed);
    AdventureUtils.sendMessageToAllParticipants(this.game, message);
  }

  private void invalidateTimer() {
    final TimeManager manager = this.game.getTimeManager();
    manager.invalidateElapsedTime();
  }

  private void stopTimer() {
    final TimeManager manager = this.game.getTimeManager();
    manager.stopTimer();
  }

  private void announceInnocentVictory() {
    final Component title = Locale.INNOCENT_VICTORY.build();
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllParticipants(this.game, title, subtitle);
    AdventureUtils.playSoundForAllParticipants(this.game, Sound.ENTITY_CAT_PURR);
  }

  private void announceMurdererVictory() {
    final Component title = Locale.MURDERER_VICTORY.build();
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllParticipants(this.game, title, subtitle);
    AdventureUtils.playSoundForAllParticipants(this.game, Sound.ENTITY_CAT_HISS);
  }
}
