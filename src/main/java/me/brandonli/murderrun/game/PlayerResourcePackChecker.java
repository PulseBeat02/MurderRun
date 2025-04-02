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
package me.brandonli.murderrun.game;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import me.brandonli.murderrun.MurderRun;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.PluginManager;

public final class PlayerResourcePackChecker implements Listener {

  private final MurderRun plugin;
  private final Set<Player> players;

  public PlayerResourcePackChecker(final MurderRun plugin) {
    this.players = Collections.newSetFromMap(new WeakHashMap<>());
    this.plugin = plugin;
  }

  public void registerEvents() {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    manager.registerEvents(this, this.plugin);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onResourcePackLoad(final PlayerResourcePackStatusEvent event) {
    final Player player = event.getPlayer();
    final PlayerResourcePackStatusEvent.Status status = event.getStatus();
    if (status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
      this.markLoaded(player);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerLeave(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    this.players.remove(player);
  }

  public void markLoaded(final Player player) {
    this.players.add(player);
  }

  public boolean isLoaded(final Player player) {
    return this.players.contains(player);
  }
}
