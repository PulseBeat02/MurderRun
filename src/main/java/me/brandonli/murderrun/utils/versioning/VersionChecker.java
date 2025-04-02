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
package me.brandonli.murderrun.utils.versioning;

import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class VersionChecker implements Listener {

  private final MurderRun plugin;
  private final String current;
  private final String latest;
  private final boolean isUpToDate;
  private final int difference;

  public VersionChecker(final MurderRun plugin) {
    this.plugin = plugin;
    this.current = VersionUtils.getCurrentCommitFromManifest();
    this.latest = VersionUtils.getLatestCommitFromGitHub();
    this.isUpToDate = this.current.equals(this.latest);
    this.difference = this.isUpToDate ? 0 : VersionUtils.getCommitsBehindCount(this.current);
  }

  public void start() {
    final Server server = Bukkit.getServer();
    final PluginManager pluginManager = server.getPluginManager();
    pluginManager.registerEvents(this, this.plugin);
  }

  public void shutdown() {
    HandlerList.unregisterAll(this);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    if (!player.isOp()) {
      return;
    }
    this.sendMessage(player);
  }

  public void sendMessage(final Player player) {
    if (this.isUpToDate) {
      return;
    }
    final AudienceProvider audienceProvider = this.plugin.getAudience();
    final BukkitAudiences audiences = audienceProvider.retrieve();
    final Audience audience = audiences.player(player);
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.runTaskLater(this.plugin, () -> audience.sendMessage(Message.PLUGIN_OUTDATED.build(this.difference, this.latest)), 10L);
  }

  public String getCurrent() {
    return this.current;
  }

  public String getLatest() {
    return this.latest;
  }

  public boolean isUpToDate() {
    return this.isUpToDate;
  }

  public int getDifference() {
    return this.difference;
  }
}
