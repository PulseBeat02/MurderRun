/*

MIT License

Copyright (c) 2025 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package me.brandonli.murderrun.utils.map;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    this.changing = new HashSet<>();
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
