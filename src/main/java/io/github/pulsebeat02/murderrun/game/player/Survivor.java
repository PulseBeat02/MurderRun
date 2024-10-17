package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;

public final class Survivor extends GamePlayer {

  private boolean canPickupCarPart;
  private boolean canPlaceBlocks;
  private boolean carPart;
  private long rewindCooldown;
  private int carPartsRetrieved;

  private final Collection<Item> glowingCarParts;
  private final Collection<GamePlayer> glowingKillers;
  private final Collection<BukkitTask> lifeInsuranceTasks;

  public Survivor(final Game game, final UUID uuid) {
    super(game, uuid);
    this.canPickupCarPart = true;
    this.glowingCarParts = new HashSet<>();
    this.glowingKillers = new HashSet<>();
    this.lifeInsuranceTasks = new HashSet<>();
  }

  public int getCarPartsRetrieved() {
    return this.carPartsRetrieved;
  }

  public void setCarPartsRetrieved(final int carPartsRetrieved) {
    this.carPartsRetrieved = carPartsRetrieved;
  }

  public void setHasCarPart(final boolean hasCarPart) {
    this.carPart = hasCarPart;
  }

  public boolean hasCarPart() {
    return this.carPart;
  }

  public void setCanPickupCarPart(final boolean canPickupCarPart) {
    this.canPickupCarPart = canPickupCarPart;
  }

  public boolean canPickupCarPart() {
    return this.canPickupCarPart;
  }

  public boolean canPlaceBlocks() {
    return this.canPlaceBlocks;
  }

  public void setCanPlaceBlocks(final boolean canPlaceBlocks) {
    this.canPlaceBlocks = canPlaceBlocks;
  }

  public long getRewindCooldown() {
    return this.rewindCooldown;
  }

  public void setRewindCooldown(final long cooldown) {
    this.rewindCooldown = cooldown;
  }

  public Collection<Item> getGlowingCarParts() {
    return this.glowingCarParts;
  }

  public Collection<GamePlayer> getGlowingKillers() {
    return this.glowingKillers;
  }

  public Collection<BukkitTask> getLifeInsuranceTasks() {
    return this.lifeInsuranceTasks;
  }
}
