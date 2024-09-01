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
    final PlayerAudience audience = gamePlayer.getAudience();
    metadata.setWorldBorderEffect(false);
    metadata.setNameTagStatus(false);
    metadata.shutdown();
    audience.removeAllBossBars();
    gamePlayer.removeAllPotionEffects();
    gamePlayer.teleport(location);
    gamePlayer.clearInventory();
    gamePlayer.setGameMode(GameMode.SURVIVAL);
    gamePlayer.setHealth(20f);
    gamePlayer.setFoodLevel(20);
    gamePlayer.setLevel(0);
    gamePlayer.setSaturation(Float.MAX_VALUE);
    gamePlayer.setFreezeTicks(0);
    gamePlayer.setWalkSpeed(0.2f);
    gamePlayer.setExp(0);
    gamePlayer.setGlowing(false);
    gamePlayer.setFireTicks(0);
    gamePlayer.stopAllSounds();
  }
}
