package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameResult;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTool;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundKeys;
import io.github.pulsebeat02.murderrun.utils.ComponentUtils;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class GamePlayerDeathEvent implements Listener {

  private final Game game;

  public GamePlayerDeathEvent(final Game game) {
    this.game = game;
  }

  public Game getGame() {
    return this.game;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerDeath(final PlayerDeathEvent event) {

    final Player player = event.getEntity();
    final Optional<GamePlayer> optional = PlayerUtils.checkIfValidEventPlayer(this.game, player);
    if (optional.isEmpty()) {
      return;
    }

    final GamePlayer gamePlayer = optional.get();
    if (this.checkDeathCancellation(gamePlayer)) {
      return;
    }

    final PlayerManager manager = this.game.getPlayerManager();
    final PlayerDeathTool death = manager.getDeathManager();
    gamePlayer.setAlive(false);
    death.initiateDeathSequence(gamePlayer);

    event.setDroppedExp(0);
    event.setKeepInventory(true);
    this.playDeathSoundEffect();
    this.runDeathTasks(gamePlayer);

    if (this.allInnocentDead()) {
      this.game.finishGame(GameResult.MURDERERS);
    }
  }

  private void runDeathTasks(final GamePlayer player) {
    final Collection<PlayerDeathTask> tasks = player.getDeathTasks();
    final Iterator<PlayerDeathTask> iterator = tasks.iterator();
    while (iterator.hasNext()) {
      final PlayerDeathTask task = iterator.next();
      task.run();
      iterator.remove();
    }
  }

  private boolean checkDeathCancellation(final GamePlayer player) {
    final Collection<PlayerDeathTask> tasks = player.getDeathTasks();
    boolean cancel;
    final Iterator<PlayerDeathTask> iterator = tasks.iterator();
    while (iterator.hasNext()) {
      final PlayerDeathTask task = iterator.next();
      cancel = task.isCancelDeath();
      if (cancel) {
        iterator.remove();
        return true;
      }
    }
    return false;
  }

  private void playDeathSoundEffect() {
    ComponentUtils.playSoundForAllParticipants(this.game, SoundKeys.DEATH);
  }

  private boolean allInnocentDead() {
    final PlayerManager manager = this.game.getPlayerManager();
    final Collection<Survivor> players = manager.getInnocentPlayers();
    return players.stream().noneMatch(GamePlayer::isAlive);
  }
}
