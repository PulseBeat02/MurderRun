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
package me.brandonli.murderrun.game.map.part;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashMap;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PartsManager {

  private final GameMap map;
  private final java.util.Map<String, CarPart> parts;

  public PartsManager(final GameMap map) {
    this.map = map;
    this.parts = new HashMap<>();
  }

  public void spawnParts() {
    this.randomizeSpawnLocations();
    this.spawnParticles();
  }

  private void randomizeSpawnLocations() {
    final Game game = this.map.getGame();
    final GameSettings configuration = game.getSettings();
    final Arena arena = requireNonNull(configuration.getArena());
    final int parts = GameProperties.CAR_PARTS_COUNT;
    for (int i = 0; i < parts; i++) {
      final Location location = arena.getRandomItemLocation();
      final CarPart part = new CarPart(location);
      final String id = part.getUuid();
      part.spawn();
      this.parts.put(id, part);
    }
  }

  private void spawnParticles() {
    final Game game = this.map.getGame();
    final GameScheduler scheduler = game.getScheduler();
    final NullReference reference = NullReference.of();
    scheduler.scheduleRepeatedTask(this::spawnParticleTask, 0, 5L, reference);
  }

  private void spawnParticleTask() {
    final Collection<CarPart> parts = this.parts.values();
    for (final CarPart stack : parts) {
      if (!stack.isPickedUp()) {
        this.spawnParticleOnPart(stack);
      }
    }
  }

  private void spawnParticleOnPart(final CarPart stack) {
    final Location location = stack.getLocation();
    final World world = requireNonNull(location.getWorld());
    location.add(0, 1, 0);
    world.spawnParticle(Particle.DUST, location, 4, 0.2, 1, 0.2, new DustOptions(Color.YELLOW, 2));
  }

  public GameMap getMap() {
    return this.map;
  }

  public java.util.Map<String, CarPart> getParts() {
    return this.parts;
  }

  public void removeCarPart(final CarPart stack) {
    final String uuid = stack.getUuid();
    this.parts.remove(uuid);
  }

  public @Nullable CarPart getCarPartItemStack(final ItemStack stack) {
    final String uuid = PDCUtils.getPersistentDataAttribute(stack, Keys.CAR_PART_UUID, PersistentDataType.STRING);
    return uuid == null ? null : this.parts.get(uuid);
  }

  public int getRemainingParts() {
    final int start = GameProperties.CAR_PARTS_COUNT;
    final int taken = start - this.parts.size();
    final int required = GameProperties.CAR_PARTS_REQUIRED;
    return Math.abs(required - taken);
  }
}
