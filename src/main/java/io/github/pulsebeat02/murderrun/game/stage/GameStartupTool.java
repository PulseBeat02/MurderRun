package io.github.pulsebeat02.murderrun.game.stage;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.GameTimer;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import java.util.function.Consumer;
import net.kyori.adventure.bossbar.BossBar;
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
    this.setBossBar();
    this.clearNetherStars();
    this.setNight();
    this.runFutureTask();
  }

  private void setNight() {
    final GameSettings settings = this.game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location spawnLocation = arena.getSpawn();
    final World world = requireNonNull(spawnLocation.getWorld());
    world.setTime(13000);
    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
    world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
    world.setGameRule(GameRule.DO_INSOMNIA, false);
  }

  private void teleportInnocentPlayers() {
    final GameSettings configuration = this.game.getSettings();
    final Arena arena = requireNonNull(configuration.getArena());
    final Location spawnLocation = arena.getSpawn();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllLivingInnocents(innocentPlayer -> innocentPlayer.teleport(spawnLocation));
  }

  private void announceHidePhase() {
    final Component title = Message.PREPARATION_PHASE.build();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.sendMessageToAllParticipants(title);
  }

  private void setBossBar() {
    final GameSettings settings = this.game.getSettings();
    final int parts = settings.getCarPartCount();
    final Component name = Message.BOSS_BAR.build(0, parts);
    final float progress = 0f;
    final BossBar.Color color = BossBar.Color.GREEN;
    final BossBar.Overlay overlay = BossBar.Overlay.NOTCHED_20;
    final PlayerManager manager = this.game.getPlayerManager();
    manager.showBossBarForAllParticipants(name, progress, color, overlay);
  }

  private void clearNetherStars() {
    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllParticipants(player -> {
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
    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllParticipants(gamePlayer -> gamePlayer.apply(player -> player.setLevel(time)));
  }

  private void announceCountdown(final int seconds) {
    final Component title = text(seconds, RED);
    final Component subtitle = empty();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.showTitleForAllParticipants(title, subtitle);
  }

  private void countDownAudio() {
    final PlayerManager manager = this.game.getPlayerManager();
    manager.playSoundForAllParticipants(Sounds.COUNTDOWN);
  }

  private void futureTask() {
    this.teleportMurderers();
    this.announceReleasePhase();
    this.playReleaseSoundEffect();
    this.spawnCarParts();
    this.playMusic();
    this.startTimer();
  }

  private void playMusic() {
    final GameScheduler scheduler = this.game.getScheduler();
    final PlayerManager manager = this.game.getPlayerManager();
    final Consumer<GamePlayer> sound = gamePlayer -> {
      final Key key = Sounds.BACKGROUND.getKey();
      final PlayerAudience audience = gamePlayer.getAudience();
      audience.playSound(key, Source.MUSIC, 0.05f, 1.0f);
    };
    scheduler.scheduleTask(() -> manager.applyToAllParticipants(sound), 5 * 20L);
  }

  private void spawnCarParts() {
    final Map map = this.game.getMap();
    final PartsManager manager = map.getCarPartManager();
    manager.spawnParts();
  }

  private void teleportMurderers() {
    final GameSettings configuration = this.game.getSettings();
    final Arena arena = requireNonNull(configuration.getArena());
    final Location spawnLocation = arena.getSpawn();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.applyToAllMurderers(murderer -> murderer.teleport(spawnLocation));
  }

  private void announceReleasePhase() {
    final Component title = Message.RELEASE_PHASE.build();
    final Component subtitle = empty();
    final PlayerManager manager = this.game.getPlayerManager();
    manager.showTitleForAllParticipants(title, subtitle);
  }

  private void playReleaseSoundEffect() {
    final PlayerManager manager = this.game.getPlayerManager();
    manager.playSoundForAllParticipants(Sounds.RELEASED_1, Sounds.RELEASED_2);
  }

  private void startTimer() {
    final GameTimer manager = this.game.getTimeManager();
    manager.startTimer();
  }

  public Game getGame() {
    return this.game;
  }
}
