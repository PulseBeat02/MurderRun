package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.MurderGame;
import io.github.pulsebeat02.murderrun.player.death.PlayerDeathManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

import java.util.UUID;

public abstract sealed class GamePlayer permits InnocentPlayer, Murderer {

  private final MurderGame game;
  private final UUID uuid;
  private boolean alive;

  public GamePlayer(final MurderGame game, final UUID uuid) {
    this.game = game;
    this.uuid = uuid;
    this.alive = true;
  }

  public abstract void onPlayerAttemptPickupPartEvent(final PlayerAttemptPickupItemEvent event);

  public void markDeath() {
    this.setAlive(false);
    this.addToGraveyard();
    this.startDeathSequence();
  }

  private void startDeathSequence() {
    final PlayerManager manager = this.game.getPlayerManager();
    final PlayerDeathManager death = manager.getDeathManager();
    death.initiateDeathSequence(this);
  }

  private void addToGraveyard() {
    final PlayerManager manager = this.game.getPlayerManager();
    manager.addDeadPlayer(this);
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(this.uuid);
  }

  public boolean isAlive() {
    return this.alive;
  }

  public void setAlive(final boolean alive) {
    this.alive = alive;
  }

  public MurderGame getGame() {
    return this.game;
  }
}
