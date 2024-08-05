package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import io.github.pulsebeat02.murderrun.game.lobby.MurderLobby;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;

public final class PlayerResetConfigurator {

  private final MurderPlayerManager manager;

  public PlayerResetConfigurator(final MurderPlayerManager manager) {
    this.manager = manager;
  }

  public void configure() {
    this.manager.applyToAllParticipants(this::handleAll);
  }

  public void handleAll(final GamePlayer gamePlayer) {
    final MurderGame game = this.manager.getGame();
    final MurderSettings configuration = game.getSettings();
    final MurderLobby lobby = configuration.getLobby();
    final Location location = lobby.getLobbySpawn();
    gamePlayer.apply(player -> {
      PlayerUtils.removeAllPotionEffects(player);
      PlayerUtils.removeAllBossBars(player);
      player.getInventory().clear();
      player.setGameMode(GameMode.SURVIVAL);
      player.teleport(location);
      player.setHealth(20f);
      player.setFoodLevel(20);
      player.setWalkSpeed(0.2f);
      player.setExp(0);
      player.setLevel(0);
      player.setSaturation(Float.MAX_VALUE);
    });
  }
}
