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
package me.brandonli.murderrun.game.extension;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.capability.Capabilities;
import me.brandonli.murderrun.game.extension.citizens.CitizensManager;
import me.brandonli.murderrun.game.extension.libsdiguises.DisguiseManager;

public final class GameExtensionManager {

  private final Game game;
  private DisguiseManager disguiseManager;
  private CitizensManager npcManager;

  public GameExtensionManager(final Game game) {
    this.game = game;
  }

  public void registerExtensions() {
    if (Capabilities.LIBSDISGUISES.isEnabled()) {
      this.disguiseManager = new DisguiseManager();
    }
    this.npcManager = new CitizensManager(this.game);
  }

  public void disableExtensions() {
    if (Capabilities.LIBSDISGUISES.isEnabled()) {
      this.disguiseManager.shutdown();
    }
    this.npcManager.shutdown();
  }

  public DisguiseManager getDisguiseManager() {
    return this.disguiseManager;
  }

  public CitizensManager getNPCManager() {
    return this.npcManager;
  }
}
