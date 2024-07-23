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
    scheduler.scheduleSyncDelayedTask(plugin, () -> this.initiateEndingSequence(winCode), 3 * 20);
  }

  private void initiateEndingSequence(final GameWinCode winCode) {
    switch (winCode) {
      case INNOCENTS -> this.announceInnocentVictory();
      case MURDERERS -> this.announceMurdererVictory();
    }
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
