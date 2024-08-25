package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameResult;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTool;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import java.util.Collection;
import java.util.Iterator;
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
    final PlayerManager manager = this.game.getPlayerManager();
    final boolean valid = manager.checkPlayerExists(player);
    if (!valid) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (this.checkDeathCancellation(gamePlayer)) {
      player.spigot().respawn();
      return;
    }

    final PlayerDeathTool death = manager.getDeathManager();
    gamePlayer.setAlive(false);
    death.initiateDeathSequence(gamePlayer);

    event.setDroppedExp(0);
    event.setKeepInventory(true);
    this.playDeathSoundEffect();
    this.runDeathTasks(gamePlayer);
    manager.resetCachedPlayers();

    if (this.allInnocentDead()) {
      this.game.finishGame(GameResult.MURDERERS);
    }

    if (this.allKillersDead()) {
      this.game.finishGame(GameResult.INNOCENTS);
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
        task.run();
        iterator.remove();
        return true;
      }
    }
    return false;
  }

  private void playDeathSoundEffect() {
    final PlayerManager manager = this.game.getPlayerManager();
    manager.playSoundForAllParticipants(Sounds.DEATH);
  }

  private boolean allInnocentDead() {
    final PlayerManager manager = this.game.getPlayerManager();
    final Collection<Survivor> players = manager.getInnocentPlayers();
    return players.stream().noneMatch(GamePlayer::isAlive);
  }

  private boolean allKillersDead() {
    final PlayerManager manager = this.game.getPlayerManager();
    final Collection<Killer> players = manager.getMurderers();
    return players.stream().noneMatch(GamePlayer::isAlive);
  }
}
