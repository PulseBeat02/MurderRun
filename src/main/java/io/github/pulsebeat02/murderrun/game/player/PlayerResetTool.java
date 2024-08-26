package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import org.bukkit.GameMode;
import org.bukkit.Location;

public final class PlayerResetTool {

  private final PlayerManager manager;

  public PlayerResetTool(final PlayerManager manager) {
    this.manager = manager;
  }

  public void configure() {
    this.manager.applyToAllParticipants(this::handlePlayer);
  }

  public void handlePlayer(final GamePlayer gamePlayer) {
    final Game game = this.manager.getGame();
    final GameSettings configuration = game.getSettings();
    final Lobby lobby = requireNonNull(configuration.getLobby());
    final Location location = lobby.getLobbySpawn();
    final MetadataManager metadata = gamePlayer.getMetadataManager();
    metadata.setWorldBorderEffect(false);
    metadata.setNameTagStatus(false);
    metadata.shutdown();
    gamePlayer.removeAllPotionEffects();
    gamePlayer.removeAllBossBars();
    gamePlayer.apply(player -> {
      player.getInventory().clear();
      player.setGameMode(GameMode.SURVIVAL);
      player.teleport(location);
      player.setHealth(20f);
      player.setFoodLevel(20);
      player.setWalkSpeed(0.2f);
      player.setExp(0);
      player.setLevel(0);
      player.setSaturation(Float.MAX_VALUE);
      player.setGlowing(false);
    });
  }
}
