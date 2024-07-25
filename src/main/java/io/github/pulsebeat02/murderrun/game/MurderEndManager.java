package io.github.pulsebeat02.murderrun.game;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitScheduler;

import static net.kyori.adventure.text.Component.empty;

public final class MurderEndManager {

  private final MurderGame game;

  public MurderEndManager(final MurderGame game) {
    this.game = game;
  }

  public void start(final MurderWinCode winCode) {
    final MurderRun plugin = this.game.getPlugin();
    this.initiateEndingSequence(winCode);
  }

  private void initiateEndingSequence(final MurderWinCode winCode) {
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
    final MurderTimeManager manager = this.game.getTimeManager();
    final long timeElapsed = manager.getElapsedTime();
    final Component message = Locale.FINAL_TIME.build(timeElapsed);
    AdventureUtils.sendMessageToAllParticipants(this.game, message);
  }

  private void invalidateTimer() {
    final MurderTimeManager manager = this.game.getTimeManager();
    manager.invalidateElapsedTime();
  }

  private void stopTimer() {
    final MurderTimeManager manager = this.game.getTimeManager();
    manager.stopTimer();
  }

  private void announceInnocentVictory() {
    final Component innocentMessage = Locale.INNOCENT_VICTORY_INNOCENT.build();
    final Component murdererMessage = Locale.INNOCENT_VICTORY_MURDERER.build();
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllInnocents(this.game, innocentMessage, subtitle);
    AdventureUtils.showTitleForAllMurderers(this.game, murdererMessage, subtitle);
    AdventureUtils.playSoundForAllInnocents(this.game, FXSound.WIN);
    AdventureUtils.playSoundForAllMurderers(this.game, FXSound.LOSS);
  }

  private void announceMurdererVictory() {
    final Component innocentMessage = Locale.MURDERER_VICTORY_INNOCENT.build();
    final Component murdererMessage = Locale.MURDERER_VICTORY_MURDERER.build();
    final Component subtitle = empty();
    AdventureUtils.showTitleForAllInnocents(this.game, innocentMessage, subtitle);
    AdventureUtils.showTitleForAllMurderers(this.game, murdererMessage, subtitle);
    AdventureUtils.playSoundForAllInnocents(this.game, FXSound.LOSS);
    AdventureUtils.playSoundForAllMurderers(this.game, FXSound.WIN);
  }
}
