/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.map.event;

import static net.kyori.adventure.text.Component.empty;

import java.util.List;
import java.util.stream.Stream;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameMode;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameResult;
import me.brandonli.murderrun.game.freezetag.FreezeTagManager;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Killer;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.player.death.DeathManager;
import me.brandonli.murderrun.game.player.death.PlayerDeathTool;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.LoosePlayerReference;
import me.brandonli.murderrun.game.statistics.PlayerStatistics;
import me.brandonli.murderrun.game.statistics.StatisticsManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public final class GamePlayerDeathEvent extends GameEvent {

  private final FreezeTagManager freezeTagManager;

  public GamePlayerDeathEvent(final Game game, final FreezeTagManager manager) {
    super(game);
    this.freezeTagManager = manager;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerRespawn(final PlayerRespawnEvent event) {
    final Player player = event.getPlayer();
    if (!this.isGamePlayer(player)) {
      return;
    }

    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final DeathManager deathManager = gamePlayer.getDeathManager();
    final GameScheduler scheduler = game.getScheduler();
    final LoosePlayerReference reference = LoosePlayerReference.of(gamePlayer);
    scheduler.scheduleTask(deathManager::runDeathTasks, 20L, reference);
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
    deathManager.setDeathLoot(drops);

    final boolean isLogging = gamePlayer.isLoggingOut();
    final Location current = player.getLocation();
    player.setLastDeathLocation(current);
    event.setDroppedExp(0);
    event.setDeathMessage(null);
    drops.clear();

    if (deathManager.checkDeathCancellation() && !isLogging) {
      event.setKeepInventory(true);
      return;
    }

    final GameMode mode = game.getMode();
    if (mode == GameMode.FREEZE_TAG && gamePlayer instanceof final Survivor survivor) {
      if (this.handleFreezeTagDeath(event, survivor, game, manager)) {
        return;
      }
    }

    this.announcePlayerDeath(player);
    final PlayerDeathTool death = manager.getDeathManager();
    gamePlayer.setAlive(false);
    death.initiateDeathSequence(gamePlayer);

    event.setKeepInventory(true);
    event.setDeathMessage(null);
    deathManager.runDeathTasks();
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

  private boolean handleFreezeTagDeath(
    final PlayerDeathEvent event,
    final Survivor survivor,
    final Game game,
    final GamePlayerManager manager
  ) {
    final GameProperties properties = game.getProperties();
    final int maxLives = properties.getFreezeTagSurvivorLives();
    final int lives = survivor.getFreezeTagLives();
    if (lives == 0) {
      survivor.setFreezeTagLives(maxLives);
    }

    final int currentLives = survivor.getFreezeTagLives();
    survivor.setFreezeTagLives(currentLives - 1);
    if (survivor.getFreezeTagLives() > 0) {
      event.setKeepInventory(true);
      event.setDeathMessage(null);

      final PlayerDeathTool death = manager.getDeathManager();
      final Player player = survivor.getInternalPlayer();
      final DeathManager deathManager = survivor.getDeathManager();
      deathManager.setCorpse(death.spawnDeadNPC(player));

      if (this.freezeTagManager != null) {
        this.freezeTagManager.freezeSurvivor(survivor);
        if (this.freezeTagManager.checkAllSurvivorsFrozen()) {
          final Component message = Message.FREEZE_TAG_ALL_FROZEN.build();
          manager.sendMessageToAllParticipants(message);
          game.finishGame(GameResult.MURDERERS);
        }
      }
      return true;
    }

    return false;
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
