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
