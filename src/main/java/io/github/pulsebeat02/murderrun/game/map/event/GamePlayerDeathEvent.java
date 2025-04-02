/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.map.event;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameResult;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTool;
import io.github.pulsebeat02.murderrun.game.statistics.PlayerStatistics;
import io.github.pulsebeat02.murderrun.game.statistics.StatisticsManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import java.util.List;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
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
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final DeathManager deathManager = gamePlayer.getDeathManager();
    final List<ItemStack> drops = event.getDrops();
    gamePlayer.setDeathLoot(drops);

    final boolean isLogging = gamePlayer.isLoggingOut();
    final Location current = player.getLocation();
    player.setLastDeathLocation(current);
    event.setDroppedExp(0);
    event.setDeathMessage(null);
    this.announcePlayerDeath(player);

    if (deathManager.checkDeathCancellation() && !isLogging) {
      event.setKeepInventory(true);
      drops.clear();
      return;
    }

    final PlayerDeathTool death = manager.getDeathManager();
    gamePlayer.setAlive(false);
    death.initiateDeathSequence(gamePlayer);

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
      if (!manager.checkPlayerExists(killer)) {
        return;
      }
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

  private void announcePlayerDeath(final Player dead) {
    final Game game = this.getGame();
    final String name = dead.getDisplayName();
    final Component title = Message.PLAYER_DEATH.build(name);
    final GamePlayerManager manager = game.getPlayerManager();
    manager.sendMessageToAllParticipants(title);
    manager.showTitleForAllParticipants(empty(), title);
  }

  private void playDeathSoundEffect() {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(Sounds.DEATH);
  }

  private boolean allInnocentDead() {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final Stream<GamePlayer> players = manager.getSurvivors();
    return players.noneMatch(GamePlayer::isAlive);
  }

  private boolean allKillersDead() {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final Stream<GamePlayer> players = manager.getKillers();
    return players.noneMatch(GamePlayer::isAlive);
  }
}
