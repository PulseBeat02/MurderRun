package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.entity.Item;

public final class Killer extends GamePlayer {

  private boolean ignoreTraps;
  private boolean forceMine;
  private long killerCooldown;
  private int kills;

  private final Collection<GamePlayer> forewarnGlowing;
  private final Collection<GamePlayer> heatSeekerGlowing;
  private final Collection<GamePlayer> floorIsLavaGlowing;
  private final Collection<GamePlayer> enderShadowsGlowing;
  private final Collection<Item> glowingTraps;

  public Killer(final Game game, final UUID uuid) {
    super(game, uuid);
    this.forceMine = true;
    this.forewarnGlowing = new HashSet<>();
    this.heatSeekerGlowing = new HashSet<>();
    this.floorIsLavaGlowing = new HashSet<>();
    this.enderShadowsGlowing = new HashSet<>();
    this.glowingTraps = new HashSet<>();
  }

  public int getKills() {
    return this.kills;
  }

  public void setKills(final int kills) {
    this.kills = kills;
  }

  public boolean isIgnoringTraps() {
    return this.ignoreTraps;
  }

  public void setIgnoreTraps(final boolean ignoreTraps) {
    this.ignoreTraps = ignoreTraps;
  }

  public void setForceMineBlocks(final boolean mineBlocks) {
    this.forceMine = mineBlocks;
  }

  public boolean canForceMineBlocks() {
    return this.forceMine;
  }

  public long getKillerRewindCooldown() {
    return this.killerCooldown;
  }

  public void setKillerRewindCooldown(final long cooldown) {
    this.killerCooldown = cooldown;
  }

  public Collection<GamePlayer> getFloorIsLavaGlowing() {
    return this.floorIsLavaGlowing;
  }

  public Collection<Item> getGlowingTraps() {
    return this.glowingTraps;
  }

  public Collection<GamePlayer> getForewarnGlowing() {
    return this.forewarnGlowing;
  }

  public Collection<GamePlayer> getHeatSeekerGlowing() {
    return this.heatSeekerGlowing;
  }

  public Collection<GamePlayer> getEnderShadowsGlowing() {
    return this.enderShadowsGlowing;
  }
}
