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

public final class Killer extends GamePlayer {

  private volatile boolean ignoreTraps;
  private volatile boolean forceMine;
  private volatile long killerCooldown;
  private volatile int kills;

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
