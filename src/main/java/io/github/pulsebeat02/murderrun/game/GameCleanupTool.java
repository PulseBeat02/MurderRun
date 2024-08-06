package io.github.pulsebeat02.murderrun.game;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundKeys;
import io.github.pulsebeat02.murderrun.utils.ComponentUtils;
import net.kyori.adventure.text.Component;

public final class GameCleanupTool {

  private final Game game;

  public GameCleanupTool(final Game game) {
    this.game = game;
  }

  public void start(final GameResult winCode) {
    this.initiateEndingSequence(winCode);
  }

  private void initiateEndingSequence(final GameResult winCode) {
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

  private void stopTimer() {
    final GameTimer manager = this.game.getTimeManager();
    manager.stopTimer();
  }

  private void announceInnocentVictory() {
    final Component innocentMessage = Locale.INNOCENT_VICTORY_INNOCENT.build();
    final Component murdererMessage = Locale.INNOCENT_VICTORY_MURDERER.build();
    final Component subtitle = empty();
    ComponentUtils.showTitleForAllInnocents(this.game, innocentMessage, subtitle);
    ComponentUtils.showTitleForAllMurderers(this.game, murdererMessage, subtitle);
    ComponentUtils.playSoundForAllInnocents(this.game, SoundKeys.WIN);
    ComponentUtils.playSoundForAllMurderers(this.game, SoundKeys.LOSS);
  }

  private void invalidateTimer() {
    final GameTimer manager = this.game.getTimeManager();
    manager.invalidateElapsedTime();
  }

  private void announceMurdererVictory() {
    final Component innocentMessage = Locale.MURDERER_VICTORY_INNOCENT.build();
    final Component murdererMessage = Locale.MURDERER_VICTORY_MURDERER.build();
    final Component subtitle = empty();
    ComponentUtils.showTitleForAllInnocents(this.game, innocentMessage, subtitle);
    ComponentUtils.showTitleForAllMurderers(this.game, murdererMessage, subtitle);
    ComponentUtils.playSoundForAllInnocents(this.game, SoundKeys.LOSS);
    ComponentUtils.playSoundForAllMurderers(this.game, SoundKeys.WIN);
  }

  private void announceMurdererTime() {
    final GameTimer manager = this.game.getTimeManager();
    final long timeElapsed = manager.getElapsedTime();
    final Component message = Locale.FINAL_TIME.build(timeElapsed);
    ComponentUtils.sendMessageToAllParticipants(this.game, message);
  }

  public Game getGame() {
    return this.game;
  }
}
