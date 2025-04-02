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
package me.brandonli.murderrun.game.map;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.Component.empty;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameResult;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class TruckManager {

  private final GameMap map;

  public TruckManager(final GameMap map) {
    this.map = map;
  }

  public void spawnParticles() {
    final Game game = this.map.getGame();
    final GameScheduler scheduler = game.getScheduler();
    final NullReference reference = NullReference.of();
    scheduler.scheduleRepeatedTask(this::spawnParticleOnTruck, 0, 2L, reference);
  }

  public void startTruckFixTimer() {
    final Game game = this.map.getGame();
    final NullReference reference = NullReference.of();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleCountdownTask(this::scheduleTruckCountdown, 5, reference);
  }

  private void scheduleTruckCountdown(final int seconds) {
    final Game game = this.map.getGame();
    if (seconds == 0) {
      game.finishGame(GameResult.INNOCENTS);
      return;
    }

    final GamePlayerManager manager = game.getPlayerManager();
    final Component msg = Message.TRUCK_STARTING_TIMER.build(seconds);
    manager.showTitleForAllParticipants(empty(), msg);
  }

  private void spawnParticleOnTruck() {
    final Game game = this.map.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location truck = arena.getTruck();
    final World world = requireNonNull(truck.getWorld());
    world.spawnParticle(Particle.LAVA, truck, 3, 1, 1, 1);
    world.spawnParticle(Particle.SMOKE, truck, 3, 1, 1, 1);
  }
}
