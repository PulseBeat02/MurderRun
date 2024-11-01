package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PlayerStartupTool {

  private final PlayerManager manager;

  public PlayerStartupTool(final PlayerManager manager) {
    this.manager = manager;
  }

  public void configurePlayers() {
    this.manager.applyToKillers(this::handleMurderer);
    this.manager.applyToLivingSurvivors(this::handleInnocent);
  }

  private void handleAll(final GamePlayer gamePlayer) {
    final Location spawn = this.getSpawnLocation();
    final PlayerAudience audience = gamePlayer.getAudience();
    final String sound = GameProperties.GAME_STARTING_SOUND;
    gamePlayer.setGameMode(GameMode.SURVIVAL);
    gamePlayer.setWalkSpeed(0.2f);
    gamePlayer.setGravity(true);
    gamePlayer.setHealth(20f);
    gamePlayer.setFoodLevel(20);
    gamePlayer.setSaturation(20);
    gamePlayer.setRespawnLocation(spawn, true);
    audience.playSound(sound);
  }

  private Location getSpawnLocation() {
    final Game game = this.manager.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    return arena.getSpawn();
  }

  public void handleInnocent(final GamePlayer gamePlayer) {
    this.handleAll(gamePlayer);
    gamePlayer.apply(player -> player.setWalkSpeed(0.2f));
  }

  public void handleMurderer(final GamePlayer gamePlayer) {
    this.handleAll(gamePlayer);
    gamePlayer.addPotionEffects(
      new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 1),
      new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, 5)
    );
    gamePlayer.setGameMode(GameMode.SURVIVAL);
    gamePlayer.setWalkSpeed(0.3f);
  }
}
