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
package me.brandonli.murderrun.resourcepack.provider.netty;

import java.nio.file.Path;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.resourcepack.provider.ProviderMethod;
import me.brandonli.murderrun.resourcepack.provider.ResourcePackProvider;
import me.brandonli.murderrun.resourcepack.provider.netty.injector.ByteBuddyBukkitInjector;
import me.brandonli.murderrun.resourcepack.provider.netty.injector.ReflectBukkitInjector;
import me.brandonli.murderrun.utils.IOUtils;
import org.bukkit.Bukkit;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class NettyHosting extends ResourcePackProvider {

  private final String url;

  public NettyHosting(final MurderRun plugin) {
    super(plugin, ProviderMethod.ON_SERVER);
    this.url = this.getPackUrl();
  }

  private String getPackUrl(@UnderInitialization NettyHosting this) {
    final String ip = IOUtils.getPublicIPAddress();
    final int port = Bukkit.getPort();
    return "http://%s:%s".formatted(ip, port);
  }

  @Override
  public String getRawUrl() {
    return this.url;
  }

  @Override
  public void start() {
    super.start();
    final Path zip = ResourcePackProvider.getServerPack();
    this.injectHandler(zip);
  }

  private void injectHandler(final Path zip) {
    try {
      final ReflectBukkitInjector injector = new ReflectBukkitInjector(zip);
      injector.inject();
    } catch (final AssertionError e) {
      final ByteBuddyBukkitInjector injector = new ByteBuddyBukkitInjector(zip);
      injector.injectAgentIntoServer();
      throw new AssertionError(e);
    }
  }
}
