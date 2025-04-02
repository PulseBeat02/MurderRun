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

import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class PlayerInviteManager {

  private final Set<CommandSender> invites;

  public PlayerInviteManager() {
    this.invites = this.createExpiringCache();
  }

  private Set<CommandSender> createExpiringCache(@UnderInitialization PlayerInviteManager this) {
    final Duration duration = Duration.ofMinutes(5);
    final Map<CommandSender, Boolean> map = CacheBuilder.newBuilder().expireAfterWrite(duration).<CommandSender, Boolean>build().asMap();
    return Collections.newSetFromMap(map);
  }

  public boolean addInvite(final CommandSender sender) {
    return this.invites.add(sender);
  }

  public Set<CommandSender> getInvites() {
    return this.invites;
  }

  public boolean removeInvite(final CommandSender sender) {
    return this.invites.remove(sender);
  }

  public boolean hasInvite(final CommandSender sender) {
    return this.invites.contains(sender);
  }
}
