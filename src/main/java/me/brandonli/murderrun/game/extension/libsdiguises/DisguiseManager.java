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
package me.brandonli.murderrun.game.extension.libsdiguises;

import java.util.ArrayList;
import java.util.Collection;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;

public final class DisguiseManager {

  private final Collection<Disguise> disguises;

  public DisguiseManager() {
    this.disguises = new ArrayList<>();
  }

  public void disguisePlayerAsOtherPlayer(final GamePlayer owner, final GamePlayer other) {
    final String name = other.getName();
    owner.apply(disguisable -> {
      final PlayerDisguise disguise = new PlayerDisguise(name);
      disguise.setEntity(disguisable);
      disguise.startDisguise();
      this.disguises.add(disguise);
    });
  }

  public void shutdown() {
    for (final Disguise disguise : this.disguises) {
      disguise.removeDisguise();
    }
  }
}
