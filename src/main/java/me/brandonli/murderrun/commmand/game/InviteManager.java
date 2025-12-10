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
package me.brandonli.murderrun.commmand.game;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class InviteManager {

  private final Map<Player, PlayerInviteManager> invites;

  public InviteManager() {
    this.invites = new HashMap<>();
  }

  public void invitePlayer(final CommandSender sender, final Player receiver) {
    final PlayerInviteManager manager = this.invites.computeIfAbsent(receiver, k -> new PlayerInviteManager());
    manager.addInvite(sender);
  }

  public void removeInvite(final CommandSender sender, final Player receiver) {
    final PlayerInviteManager manager = this.invites.get(receiver);
    if (manager == null) {
      return;
    }

    manager.removeInvite(sender);
  }

  public boolean hasInvite(final CommandSender sender, final Player receiver) {
    final PlayerInviteManager manager = this.invites.get(receiver);
    if (manager == null) {
      return false;
    }

    return manager.hasInvite(sender);
  }
}
