package io.github.pulsebeat02.murderrun.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public sealed class GamePlayer permits InnocentPlayer, Murderer {

  private final UUID uuid;
  private boolean alive;

  public GamePlayer(final UUID uuid) {
    this.uuid = uuid;
    this.alive = true;
  }

  public void markDeath() {
    this.setAlive(false);

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
}
