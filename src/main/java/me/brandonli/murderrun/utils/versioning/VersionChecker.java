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
