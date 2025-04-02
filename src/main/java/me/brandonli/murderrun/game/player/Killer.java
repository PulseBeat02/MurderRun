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
package me.brandonli.murderrun.game.player;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import me.brandonli.murderrun.game.Game;
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
