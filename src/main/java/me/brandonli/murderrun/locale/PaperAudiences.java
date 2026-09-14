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
package me.brandonli.murderrun.locale;

import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Wrapper for Audience since we previously used BukkitAudiences, which has since been removed.
 */
public final class PaperAudiences {

  private final Server server;

  PaperAudiences(final Server server) {
    this.server = server;
  }

  public Audience console() {
    return this.server.getConsoleSender();
  }

  public Audience sender(final CommandSender sender) {
    return sender;
  }

  public Audience player(final Player player) {
    return player;
  }

  public Audience player(final UUID uuid) {
    final Player player = this.server.getPlayer(uuid);
    if (player == null) {
      return Audience.empty();
    }
    return player;
  }
}
