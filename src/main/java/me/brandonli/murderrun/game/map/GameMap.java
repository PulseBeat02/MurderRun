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

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.map.event.GameEventManager;
import me.brandonli.murderrun.game.map.part.PartsManager;

public final class GameMap {

  private final Game game;

  private PartsManager partsManager;
  private GameEventManager eventManager;
  private TruckManager truckManager;
  private BlockWhitelistManager blockWhitelistManager;

  public GameMap(final Game game) {
    this.game = game;
  }

  public void start() {
    this.partsManager = new PartsManager(this);
    this.eventManager = new GameEventManager(this);
    this.truckManager = new TruckManager(this);
    this.blockWhitelistManager = new BlockWhitelistManager();
    this.eventManager.registerEvents();
    this.truckManager.spawnParticles();
  }

  public GameEventManager getEventManager() {
    return this.eventManager;
  }

  public void shutdown() {
    this.unregisterEvents();
  }

  private void unregisterEvents() {
    this.eventManager.unregisterEvents();
  }

  public Game getGame() {
    return this.game;
  }

  public PartsManager getCarPartManager() {
    return this.partsManager;
  }

  public TruckManager getTruckManager() {
    return this.truckManager;
  }

  public BlockWhitelistManager getBlockWhitelistManager() {
    return this.blockWhitelistManager;
  }
}
