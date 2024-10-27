package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameResult;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTool;
import io.github.pulsebeat02.murderrun.game.statistics.PlayerStatistics;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import java.util.Collection;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

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
    final List<ItemStack> drops = event.getDrops();
    final boolean isLogging = gamePlayer.isLoggingOut();
    final Location current = player.getLocation();
    player.setLastDeathLocation(current);
    event.setDroppedExp(0);

    if (deathManager.checkDeathCancellation() && !isLogging) {
      event.setKeepInventory(true);
      drops.clear();
      return;
    }

    final PlayerDeathTool death = manager.getDeathManager();
    gamePlayer.setAlive(false);
    death.initiateDeathSequence(gamePlayer);
    manager.resetCachedPlayers();

    event.setKeepInventory(true);
    event.setDeathMessage(null);
    deathManager.runDeathTasks();
    drops.clear();
    this.playDeathSoundEffect();

    final MurderRun plugin = game.getPlugin();
    final StatisticsManager statistics = plugin.getStatisticsManager();
    final PlayerStatistics stats = statistics.getOrCreatePlayerStatistic(gamePlayer);
    stats.incrementTotalDeaths();

    final DamageSource source = event.getDamageSource();
    final Entity cause = source.getCausingEntity();
    if (cause instanceof final Player killer) {
      final GamePlayer other = manager.getGamePlayer(killer);
      final PlayerStatistics otherStats = statistics.getOrCreatePlayerStatistic(other);
      otherStats.incrementTotalKills();
      if (other instanceof final Killer killer1) {
        final int kills = killer1.getKills();
        killer1.setKills(kills + 1);
      }
    }
    plugin.updatePluginData();

    if (this.allInnocentDead()) {
      game.finishGame(GameResult.MURDERERS);
    }

    if (this.allKillersDead()) {
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
