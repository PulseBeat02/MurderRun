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
package me.brandonli.murderrun.game.lobby.event;

import java.util.Collection;
import java.util.Set;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.lobby.PreGameManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public final class PreGameEvents {

  private final PreGameManager manager;
  private final Collection<Listener> events;

  public PreGameEvents(final PreGameManager manager) {
    this.manager = manager;
    this.events = Set.of(
      new PlayerItemDropListener(manager),
      new PlayerDamagePreventionListener(manager),
      new PlayerLeaveListener(manager),
      new PlayerBlockModifyListener(manager),
      new PlayerProjectileListener(manager),
      new PlayerArmorEvent(manager),
      new PlayerRightClickEvent(manager),
      new PlayerInventoryClickEvent(manager)
    );
  }

  public void registerEvents() {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    final MurderRun plugin = this.manager.getPlugin();
    this.events.forEach(event -> manager.registerEvents(event, plugin));
  }

  public void unregisterEvents() {
    this.events.forEach(HandlerList::unregisterAll);
  }
}
