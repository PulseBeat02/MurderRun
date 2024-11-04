/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
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
