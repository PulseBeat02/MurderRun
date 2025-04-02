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
package me.brandonli.murderrun.game.gadget;

import com.google.common.util.concurrent.AtomicDouble;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;

public final class GadgetManager {

  private final MurderRun plugin;
  private final Game game;
  private final AtomicDouble activationRange;

  private GadgetLoadingMechanism mechanism;
  private GadgetActionHandler actionHandler;

  public GadgetManager(final Game game) {
    final MurderRun plugin = game.getPlugin();
    this.game = game;
    this.plugin = plugin;
    this.activationRange = new AtomicDouble(2);
  }

  public void start() {
    this.mechanism = new GadgetLoadingMechanism(this);
    this.actionHandler = new GadgetActionHandler(this);
    this.actionHandler.start();
  }

  public void shutdown() {
    this.mechanism.shutdown();
    this.actionHandler.shutdown();
  }

  public GadgetLoadingMechanism getMechanism() {
    return this.mechanism;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public Game getGame() {
    return this.game;
  }

  public double getActivationRange() {
    return this.activationRange.get();
  }

  public void setActivationRange(final double range) {
    this.activationRange.getAndSet(range);
  }
}
