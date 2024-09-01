package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameResult;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTool;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import java.util.Collection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class GamePlayerDeathEvent extends GameEvent {

  public GamePlayerDeathEvent(final Game game) {
    super(game);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerDeath(final PlayerDeathEvent event) {

    final Player player = event.getEntity();
    if (!this.isGamePlayer(player)) {
      return;
    }

    final Game game = this.getGame();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final DeathManager deathManager = gamePlayer.getDeathManager();
    final Spigot spigot = player.spigot();
    if (deathManager.checkDeathCancellation()) {
      spigot.respawn();
      return;
    }

    final PlayerDeathTool death = manager.getDeathManager();
    gamePlayer.setAlive(false);
    death.initiateDeathSequence(gamePlayer);

    event.setDroppedExp(0);
    event.setKeepInventory(true);
    event.setDeathMessage(null);
    this.playDeathSoundEffect();

    deathManager.runDeathTasks();
    manager.resetCachedPlayers();

    if (this.allInnocentDead()) {
      spigot.respawn();
      game.finishGame(GameResult.MURDERERS);
    }

    if (this.allKillersDead()) {
      spigot.respawn();
      game.finishGame(GameResult.INNOCENTS);
    }
  }

  private void playDeathSoundEffect() {
    final Game game = this.getGame();
    final PlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(Sounds.DEATH);
  }

  private boolean allInnocentDead() {
    final Game game = this.getGame();
    final PlayerManager manager = game.getPlayerManager();
    final Collection<Survivor> players = manager.getInnocentPlayers();
    return players.stream().noneMatch(GamePlayer::isAlive);
  }

  private boolean allKillersDead() {
    final Game game = this.getGame();
    final PlayerManager manager = game.getPlayerManager();
    final Collection<Killer> players = manager.getMurderers();
    return players.stream().noneMatch(GamePlayer::isAlive);
  }
}
