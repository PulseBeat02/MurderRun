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
package io.github.pulsebeat02.murderrun.game.map;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class TruckManager {

  private final Map map;

  public TruckManager(final Map map) {
    this.map = map;
  }

  public void spawnParticles() {
    final Game game = this.map.getGame();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(this::spawnParticleOnTruck, 0, 4L);
  }

  private void spawnParticleOnTruck() {
    final Game game = this.map.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location truck = arena.getTruck();
    final World world = requireNonNull(truck.getWorld());
    world.spawnParticle(Particle.LAVA, truck, 3, 0.5, 0.5, 0.5);
    world.spawnParticle(Particle.SMOKE, truck, 3, 0.5, 0.5, 0.5);
  }
}
