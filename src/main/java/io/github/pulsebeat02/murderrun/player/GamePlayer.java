package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import io.github.pulsebeat02.murderrun.lobby.MurderLobby;
import io.github.pulsebeat02.murderrun.player.death.PlayerDeathManager;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import java.util.UUID;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityPickupItemEvent;

public abstract sealed class GamePlayer permits InnocentPlayer, Murderer {

  private final MurderGame game;
  private final UUID uuid;
  private boolean alive;

  public GamePlayer(final MurderGame game, final UUID uuid) {
    this.game = game;
    this.uuid = uuid;
    this.alive = true;
  }

  public abstract void onPlayerAttemptPickupPartEvent(final EntityPickupItemEvent event);

  public void onMatchStart() {
    final Player player = this.getPlayer();
    player.setHealth(20f);
    player.setFoodLevel(20);
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(this.uuid);
  }

  public void onMatchReset() {
    final MurderSettings configuration = this.game.getSettings();
    final MurderLobby lobby = configuration.getLobby();
    final Location location = lobby.getLobbySpawn();
    final Player player = this.getPlayer();
    PlayerUtils.removeAllPotionEffects(player);
    player.getInventory().clear();
    player.setGameMode(GameMode.SURVIVAL);
    player.teleport(location);
    player.setHealth(20f);
    player.setFoodLevel(20);
    player.setWalkSpeed(0.2f);
    player.setExp(0);
    player.setLevel(0);
  }

  public void markDeath() {
    this.setAlive(false);
    this.startDeathSequence();
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public boolean isAlive() {
    return this.alive;
  }

  public void setAlive(final boolean alive) {
    this.alive = alive;
  }

  private void startDeathSequence() {
    final PlayerManager manager = this.game.getPlayerManager();
    final PlayerDeathManager death = manager.getDeathManager();
    death.initiateDeathSequence(this);
  }

  public MurderGame getGame() {
    return this.game;
  }
}
