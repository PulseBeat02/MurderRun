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
package me.brandonli.murderrun.game.stage;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

import java.util.function.Consumer;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.GameTimer;
import me.brandonli.murderrun.game.GameTimerUpdater;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.map.part.PartsManager;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

public final class GameStartupTool {

  private final Game game;

  public GameStartupTool(final Game game) {
    this.game = game;
  }

  public void start() {
    this.teleportInnocentPlayers();
    this.announceHidePhase();
    this.clearSurvivorNetherStars();
    this.setEnvironment();
    this.runFutureTask();
  }

  private void setEnvironment() {
    final GameSettings settings = this.game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location spawnLocation = arena.getSpawn();
    final World world = requireNonNull(spawnLocation.getWorld());
    world.setTime(13000);
    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
    world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
    world.setGameRule(GameRule.DO_INSOMNIA, false);
    world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
  }

  private void teleportInnocentPlayers() {
    final GameSettings configuration = this.game.getSettings();
    final Arena arena = requireNonNull(configuration.getArena());
    final Location spawnLocation = arena.getSpawn();
    final GamePlayerManager manager = this.game.getPlayerManager();
    final GameScheduler scheduler = this.game.getScheduler();
    manager.applyToLivingSurvivors(player -> {
      final StrictPlayerReference reference = StrictPlayerReference.of(player);
      player.setInvulnerable(true);
      player.addPotionEffects(PotionEffectType.BLINDNESS.createEffect(5 * 20, 1));
      player.addPotionEffects(PotionEffectType.NAUSEA.createEffect(3 * 20, 1));
      player.teleport(spawnLocation);
      scheduler.scheduleTask(() -> player.setInvulnerable(false), 5 * 20L, reference);
    });
  }

  private void announceHidePhase() {
    final Component title = Message.PREPARATION_PHASE.build();
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.sendMessageToAllParticipants(title);
  }

  private void clearSurvivorNetherStars() {
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.applyToSurvivors(player -> {
      final PlayerInventory inventory = player.getInventory();
      inventory.remove(Material.NETHER_STAR);
    });
  }

  private void clearKillerNetherStars() {
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.applyToKillers(player -> {
      final PlayerInventory inventory = player.getInventory();
      inventory.remove(Material.NETHER_STAR);
    });
  }

  private void runFutureTask() {
    final GameProperties properties = this.game.getProperties();
    final int seconds = properties.getBeginningStartingTime();
    final GameScheduler scheduler = this.game.getScheduler();
    final NullReference reference = NullReference.of();
    scheduler.scheduleCountdownTask(this::handleCountdownSeconds, seconds, reference);
  }

  private void handleCountdownSeconds(final int time) {
    switch (time) {
      case 5 -> this.startAudioCountdown();
      case 4 -> GameStartupTool.this.announceCountdown(4);
      case 3 -> GameStartupTool.this.announceCountdown(3);
      case 2 -> GameStartupTool.this.announceCountdown(2);
      case 1 -> GameStartupTool.this.announceCountdown(1);
      case 0 -> GameStartupTool.this.futureTask();
      default -> {} // Do nothing
    }
    GameStartupTool.this.setTimeRemaining(time);
  }

  private void startAudioCountdown() {
    GameStartupTool.this.countDownAudio();
    GameStartupTool.this.announceCountdown(5);
  }

  private void setTimeRemaining(final int time) {
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllParticipants(gamePlayer -> gamePlayer.apply(player -> player.setLevel(time)));
  }

  private void announceCountdown(final int seconds) {
    final Component title = text(seconds, RED);
    final Component subtitle = empty();
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.showTitleForAllParticipants(title, subtitle);
  }

  private void countDownAudio() {
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.playSoundForAllParticipants(Sounds.COUNTDOWN);
  }

  private void futureTask() {
    this.clearKillerNetherStars();
    this.teleportMurderers();
    this.announceReleasePhase();
    this.playReleaseSoundEffect();
    this.spawnCarParts();
    this.playMusic();
    this.startTimer();
    this.setGameStatus();
  }

  private void setGameStatus() {
    final GameStatus status = this.game.getStatus();
    status.setStatus(GameStatus.Status.KILLERS_RELEASED);
  }

  private void playMusic() {
    final GameScheduler scheduler = this.game.getScheduler();
    final GamePlayerManager manager = this.game.getPlayerManager();
    final Consumer<GamePlayer> sound = gamePlayer -> {
      final Key key = Sounds.BACKGROUND.getKey();
      final PlayerAudience audience = gamePlayer.getAudience();
      audience.playSound(key, Source.MASTER, 0.01f, 1.0f);
    };
    final NullReference reference = NullReference.of();
    scheduler.scheduleTask(() -> manager.applyToAllParticipants(sound), 5 * 20L, reference);
  }

  private void spawnCarParts() {
    final GameMap map = this.game.getMap();
    final PartsManager manager = map.getCarPartManager();
    manager.spawnParts();
  }

  private void teleportMurderers() {
    final GameSettings configuration = this.game.getSettings();
    final Arena arena = requireNonNull(configuration.getArena());
    final Location spawnLocation = arena.getSpawn();
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.applyToKillers(murderer -> murderer.teleport(spawnLocation));
  }

  private void announceReleasePhase() {
    final Component title = Message.RELEASE_PHASE.build();
    final Component subtitle = empty();
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.showTitleForAllParticipants(title, subtitle);
  }

  private void playReleaseSoundEffect() {
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager.playSoundForAllParticipants(Sounds.RELEASED_1, Sounds.RELEASED_2);
  }

  private void startTimer() {
    final GameTimer manager = this.game.getTimeManager();
    final GameTimerUpdater updater = new GameTimerUpdater(this.game);
    manager.startTimer(this.game);
    updater.start();
  }

  public Game getGame() {
    return this.game;
  }
}
