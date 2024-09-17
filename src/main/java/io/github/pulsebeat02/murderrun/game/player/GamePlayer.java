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

  private long lastPortalUse;
  private boolean canDismount;
  private boolean canSpectatorTeleport;
  private boolean alive;
  private boolean loggingOut;

  public GamePlayer(final Game game, final UUID uuid) {
    this.game = game;
    this.uuid = uuid;
    this.alive = true;
    this.canDismount = true;
  }

  public void start() {
    this.audience = new PlayerAudience(this.game, this.uuid);
    this.metadata = new MetadataManager(this);
    this.deathManager = new DeathManager();
    this.metadata.start();
  }

  @Override
  public Player getInternalPlayer() {
    return requireNonNull(Bukkit.getPlayer(this.uuid));
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

  @Override
  public boolean canDismount() {
    return this.canDismount;
  }

  @Override
  public void setCanDismount(final boolean canDismount) {
    this.canDismount = canDismount;
  }

  @Override
  public void setAllowSpectatorTeleport(final boolean allow) {
    this.canSpectatorTeleport = allow;
  }

  @Override
  public boolean canSpectatorTeleport() {
    return this.canSpectatorTeleport;
  }

  @Override
  public void setLastPortalUse(final long cooldown) {
    this.lastPortalUse = cooldown;
  }

  @Override
  public long getLastPortalUse() {
    return this.lastPortalUse;
  }

  @Override
  public boolean isLoggingOut() {
    return this.loggingOut;
  }

  @Override
  public void setLoggingOut(final boolean loggingOut) {
    this.loggingOut = loggingOut;
  }
}
