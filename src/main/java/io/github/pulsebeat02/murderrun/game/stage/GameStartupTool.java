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
package io.github.pulsebeat02.murderrun.game.stage;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.GameStatus;
import io.github.pulsebeat02.murderrun.game.GameTimer;
import io.github.pulsebeat02.murderrun.game.GameTimerUpdater;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.map.GameMap;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import java.util.function.Consumer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.PlayerInventory;

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
    manager.applyToLivingSurvivors(innocentPlayer -> innocentPlayer.teleport(spawnLocation));
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
    final int seconds = GameProperties.BEGINNING_STARTING_TIME;
    final GameScheduler scheduler = this.game.getScheduler();
    scheduler.scheduleCountdownTask(this::handleCountdownSeconds, seconds);
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
      audience.playSound(key, Source.MUSIC, 0.1f, 1.0f);
    };
    scheduler.scheduleTask(() -> manager.applyToAllParticipants(sound), 5 * 20L);
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
    manager.startTimer();
    updater.start();
  }

  public Game getGame() {
    return this.game;
  }
}
