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
package me.brandonli.murderrun.api.event.contract.gadget;

import me.brandonli.murderrun.api.event.Cancellable;
import me.brandonli.murderrun.api.event.MurderRunEvent;
import me.brandonli.murderrun.api.event.generated.Param;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.gadget.Gadget;
import me.brandonli.murderrun.game.player.GamePlayer;

public interface GadgetUseEvent extends MurderRunEvent, Cancellable {
  @Param(0)
  Gadget getGadget();

  @Param(1)
  GamePlayer getPlayer();

  default Game getGame() {
    final GamePlayer player = this.getPlayer();
    return player.getGame();
  }
}
