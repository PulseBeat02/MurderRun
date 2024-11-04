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
package io.github.pulsebeat02.murderrun.game.map.part;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PartsManager {

  private final Map map;
  private final java.util.Map<String, CarPart> parts;

  public PartsManager(final Map map) {
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
    scheduler.scheduleRepeatedTask(this::spawnParticleTask, 0, 5L);
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
    world.spawnParticle(Particle.DUST, location, 4, 0.2, 1, 0.2, new DustOptions(Color.YELLOW, 2));
  }

  public Map getMap() {
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
    return this.parts.size();
  }
}
