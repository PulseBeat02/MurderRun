/*

MIT License

Copyright (c) 2024 Brandon Li

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
