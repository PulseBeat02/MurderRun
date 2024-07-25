package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.game.MurderWinCode;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.GamePlayer;
import io.github.pulsebeat02.murderrun.player.InnocentPlayer;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.resourcepack.sound.FXSound;
import io.github.pulsebeat02.murderrun.utils.AdventureUtils;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Collection;
import java.util.Optional;

public final class GamePlayerDeathEvent implements Listener {

  private final MurderGame game;

  public GamePlayerDeathEvent(final MurderGame game) {
    this.game = game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerDeath(final PlayerDeathEvent event) {
    final Player player = event.getEntity();
    final Optional<GamePlayer> optional = PlayerUtils.checkIfValidPlayer(this.game, player);
    if (optional.isEmpty()) {
      return;
    }
    final GamePlayer gamePlayer = optional.get();
    gamePlayer.markDeath();
    this.playDeathSoundEffect();
    if (this.allInnocentDead()) {
      this.game.finishGame(MurderWinCode.MURDERERS);
    }
  }

  private boolean allInnocentDead() {
    final PlayerManager manager = this.game.getPlayerManager();
    final Collection<InnocentPlayer> players = manager.getInnocentPlayers();
    return players.stream().noneMatch(GamePlayer::isAlive);
  }

  private void playDeathSoundEffect() {
    AdventureUtils.playSoundForAllParticipants(this.game, FXSound.DEATH);
  }
}
