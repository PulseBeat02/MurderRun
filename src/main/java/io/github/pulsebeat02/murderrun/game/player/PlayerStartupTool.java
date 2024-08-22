package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
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
    this.manager.applyToAllMurderers(this::handleMurderer);
    this.manager.applyToAllLivingInnocents(this::handleInnocent);
  }

  private void handleAll(final GamePlayer gamePlayer) {
    gamePlayer.apply(player -> {
      player.setGameMode(GameMode.ADVENTURE);
      player.setWalkSpeed(0.2f);
      player.setGravity(true);
      player.setHealth(20f);
      player.setFoodLevel(20);
      player.setSaturation(20);
      player.setRespawnLocation(this.getSpawnLocation());
    });
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
        new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
    gamePlayer.apply(player -> {
      player.setGameMode(GameMode.SURVIVAL);
      player.setWalkSpeed(0.3f);
    });
  }
}
