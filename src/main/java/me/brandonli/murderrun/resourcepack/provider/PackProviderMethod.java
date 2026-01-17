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
package me.brandonli.murderrun.resourcepack.provider;

import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.data.yaml.PluginDataConfigurationMapper;
import me.brandonli.murderrun.resourcepack.provider.http.ServerPackHosting;
import me.brandonli.murderrun.resourcepack.provider.netty.NettyHosting;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public final class PackProviderMethod {

  private final MurderRun plugin;

  public PackProviderMethod(final MurderRun plugin) {
    this.plugin = plugin;
  }

  public ResourcePackProvider getProvider() {
    final PluginDataConfigurationMapper config = this.plugin.getConfiguration();
    final ProviderMethod method = config.getProviderMethod();
    final ResourcePackProvider provider =
        switch (method) {
          case MC_PACK_HOSTING -> new MCPackHosting(this.plugin);
          case LOCALLY_HOSTED_DAEMON -> {
            final String hostName = config.getHostName();
            final int port = config.getPort();
            yield new ServerPackHosting(this.plugin, hostName, port);
          }
          case ON_SERVER -> new NettyHosting(this.plugin);
        };
    this.deferCaching(provider);
    return provider;
  }

  private void deferCaching(final ResourcePackProvider provider) {
    // wait for netty because url isn't valid yet
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.runTaskAsynchronously(this.plugin, provider::cachePack);
  }
}
