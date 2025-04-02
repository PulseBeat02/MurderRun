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
package me.brandonli.murderrun.resourcepack.provider.http;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.resourcepack.provider.ProviderMethod;
import me.brandonli.murderrun.resourcepack.provider.ResourcePackProvider;

public final class ServerPackHosting extends ResourcePackProvider {

  private static final String HOST_URL = "http://%s:%s";

  private final String hostName;
  private final int port;

  private FileHttpServer server;

  public ServerPackHosting(final MurderRun plugin, final String hostName, final int port) {
    super(plugin, ProviderMethod.LOCALLY_HOSTED_DAEMON);
    this.hostName = requireNonNull(hostName);
    this.port = port;
  }

  @Override
  public String getRawUrl() {
    return HOST_URL.formatted(this.hostName, this.port);
  }

  @Override
  public void start() {
    super.start();
    final Path zip = ResourcePackProvider.getServerPack();
    this.server = new FileHttpServer(this.port, zip);
    this.server.start();
  }

  @Override
  public void shutdown() {
    super.shutdown();
    if (this.server == null) {
      return;
    }
    this.server.stop();
  }

  public String getHostName() {
    return this.hostName;
  }

  public int getPort() {
    return this.port;
  }

  public FileHttpServer getServer() {
    return this.server;
  }
}
