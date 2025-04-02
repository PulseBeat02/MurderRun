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
package me.brandonli.murderrun.utils.map;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.lobby.GameManager;
import me.brandonli.murderrun.game.lobby.PreGameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;

public final class MapTeleportSkipListener extends SimplePacketListenerAbstract implements Listener {

  private final MurderRun plugin;
  private final Set<Player> changing;

  public MapTeleportSkipListener(final MurderRun plugin) {
    this.plugin = plugin;
    this.changing = ConcurrentHashMap.newKeySet();
  }

  public void start() {
    final Server server = Bukkit.getServer();
    final PluginManager pluginManager = server.getPluginManager();
    pluginManager.registerEvents(this, this.plugin);

    final PacketEventsAPI<?> api = PacketEvents.getAPI();
    final EventManager eventManager = api.getEventManager();
    eventManager.registerListener(this);
  }

  public void shutdown() {
    final PacketEventsAPI<?> api = PacketEvents.getAPI();
    final EventManager eventManager = api.getEventManager();
    HandlerList.unregisterAll(this);
    eventManager.unregisterListener(this);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerTeleportWorld(final PlayerTeleportEvent event) {
    final Location from = event.getFrom();
    final Location to = event.getTo();
    if (to == null) {
      return;
    }

    final World fromWorld = from.getWorld();
    final World toWorld = to.getWorld();
    if (fromWorld == null || toWorld == null) {
      return;
    }

    if (fromWorld.equals(toWorld)) {
      return;
    }

    final Player player = event.getPlayer();
    final GameManager gameManager = this.plugin.getGameManager();
    final Map<String, PreGameManager> games = gameManager.getGames();
    final Collection<PreGameManager> preGameManagers = games.values();
    for (final PreGameManager preGameManager : preGameManagers) {
      final GameSettings gameSettings = preGameManager.getSettings();
      final World gameWorld = gameSettings.getWorld();
      if (gameWorld == null) {
        continue;
      }
      if (gameWorld.equals(fromWorld) || gameWorld.equals(toWorld)) {
        this.changing.add(player);
      }
    }
  }

  @Override
  public void onPacketPlaySend(final PacketPlaySendEvent event) {
    final PacketType.Play.Server packetType = event.getPacketType();
    if (packetType != PacketType.Play.Server.RESPAWN) {
      return;
    }

    final Player player = event.getPlayer();
    if (player == null) {
      return;
    }

    if (this.changing.remove(player)) {
      event.setCancelled(true);
    }
  }
}
