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
package me.brandonli.murderrun.game.ability.survivor;
//
// import io.github.pulsebeat02.murderrun.game.Game;
// import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
// import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
// import org.bukkit.Location;
// import org.bukkit.World;
// import org.bukkit.entity.TextDisplay;
// import org.bukkit.entity.Zombie;
//
// public final class SadieAbility implements Ability {
//
//  @Override
//  public boolean checkActivation() {
//    return true;
//  }
//
//  @Override
//  public void applyAbility(final Game game, final GamePlayer player) {
//
//    Location location = player.getLocation();
//    World world = location.getWorld();
//    TextDisplay display = world.spawn(location, TextDisplay.class);
//    display.setText("JOEL");
//
//    GameScheduler scheduler = game.getScheduler();
//    scheduler.scheduleRepeatedTask(() -> {
//      location.add(0, 1,0);
//      display.teleport(location);
//    }, 0, 5, Integer.MAX_VALUE);
//
////    for (int i = 0; i < 20; i++) {
////      location.add(0, 1,0);
////      display.teleport(location);
////    }
//
//  }
//
//  @Override
//  public Runnable getTask() {
//    return null;
//  }
// }
