/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.player;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import me.brandonli.murderrun.game.Game;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;

public final class Survivor extends GamePlayer {

  private boolean canPickupCarPart;
  private boolean canPlaceBlocks;
  private boolean carPart;
  private boolean canSee;
  private boolean heardSound;

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

  public boolean canSee() {
    return canSee;
  }

  public void setCanSee(final boolean canSee) {
    this.canSee = canSee;
  }

  public boolean getHeardSound() {
    return heardSound;
  }

  public void setHeardSound(final boolean heardSound) {
    this.heardSound = heardSound;
  }
}
