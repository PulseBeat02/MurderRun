package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GamePlayer extends AbstractPlayer {

  private final Game game;
  private final UUID uuid;

  private MetadataManager metadata;
  private DeathManager deathManager;
  private PlayerAudience audience;

  private boolean alive;

  public GamePlayer(final Game game, final UUID uuid) {
    this.game = game;
    this.uuid = uuid;
    this.alive = true;
  }

  public void start() {
    this.audience = new PlayerAudience(this.game, this.uuid);
    this.metadata = new MetadataManager(this);
    this.deathManager = new DeathManager();
  }

  @Override
  public Player getInternalPlayer() {
    return requireNonNull(Bukkit.getPlayer(this.uuid));
  }

  @Override
  public UUID getUUID() {
    return this.uuid;
  }

  @Override
  public boolean isAlive() {
    return this.alive;
  }

  @Override
  public void setAlive(final boolean alive) {
    this.alive = alive;
  }

  @Override
  public Game getGame() {
    return this.game;
  }

  @Override
  public MetadataManager getMetadataManager() {
    return this.metadata;
  }

  @Override
  public DeathManager getDeathManager() {
    return this.deathManager;
  }

  @Override
  public PlayerAudience getAudience() {
    return this.audience;
  }
}
