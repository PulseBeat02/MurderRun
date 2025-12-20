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

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.capability.Capabilities;
import me.brandonli.murderrun.game.extension.craftengine.CraftEngineManager;
import me.brandonli.murderrun.game.extension.nexo.NexoManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.PackWrapper;
import me.brandonli.murderrun.utils.IOUtils;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ResourcePackProvider implements PackProvider {

  private static final Path SERVER_PACK;

  static {
    try {
      final PackWrapper wrapper = new PackWrapper();
      SERVER_PACK = wrapper.wrapPack();
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private final ProviderMethod method;
  private final MurderRun plugin;

  private ResourcePackRequest cached;
  private String url;

  public ResourcePackProvider(final MurderRun plugin, final ProviderMethod method) {
    this.plugin = plugin;
    this.method = method;
  }

  public void cachePack() {
    final Stream<ResourcePackInfo> customPacks = this.getCustomPackInfo().stream();
    final Stream<ResourcePackInfo> existing = Stream.of(this.getMainResourceInfo(), this.cacheProvidedResourcesExceptionally()).flatMap(
      Optional::stream
    );
    final List<ResourcePackInfo> infos = Stream.concat(customPacks, existing).toList();
    final Component message = Message.RESOURCEPACK_PROMPT.build();
    final ResourcePackRequest.Builder builder = ResourcePackRequest.resourcePackRequest();
    final boolean required = GameProperties.FORCE_RESOURCEPACK;
    this.cached = builder.required(required).packs(infos).prompt(message).asResourcePackRequest();
  }

  private Optional<ResourcePackInfo> getMainResourceInfo() {
    final String url = this.getFinalUrl();
    final URI uri = URI.create(url);
    final String hash = IOUtils.getSHA1Hash(uri);
    final ResourcePackInfo info = ResourcePackInfo.resourcePackInfo().uri(uri).hash(hash).build();
    return Optional.of(info);
  }

  private Collection<ResourcePackInfo> getCustomPackInfo() {
    if (Capabilities.NEXO.isEnabled()) {
      final NexoManager manager = this.plugin.getNexoManager();
      final ResourcePackInfo packInfo = manager.getPackInfo();
      return List.of(packInfo);
    }
    if (Capabilities.CRAFTENGINE.isEnabled()) {
      final CraftEngineManager manager = this.plugin.getCraftEngineManager();
      final Collection<ResourcePackInfo> packInfo = manager.getPackInfo();
      return List.copyOf(packInfo);
    }
    return List.of();
  }

  private Optional<ResourcePackInfo> cacheProvidedResourcesExceptionally() {
    try {
      return this.cacheProvidedResources();
    } catch (final AssertionError e) {
      return Optional.empty();
    }
  }

  private Optional<ResourcePackInfo> cacheProvidedResources() {
    try {
      final ResourcePackInfo info = this.getResourceInfo();
      return Optional.ofNullable(info);
    } catch (final AssertionError e) {
      final String msg =
        "Timed-out while retrieving resource pack hash! Consider changing the resource pack provider if currently set to MC_PACK_HOSTING!";
      throw new AssertionError(msg);
    }
  }

  private @Nullable ResourcePackInfo getResourceInfo() {
    final String url = GameProperties.BUILT_IN_RESOURCES;
    if (url.equalsIgnoreCase("none")) {
      return null;
    }
    final URI uri = URI.create(url);
    final String hash = IOUtils.getSHA1Hash(uri);
    return ResourcePackInfo.resourcePackInfo().uri(uri).hash(hash).build();
  }

  public abstract String getRawUrl();

  @Override
  public ResourcePackRequest getResourcePackRequest() {
    return this.cached;
  }

  @Override
  public void start() {}

  @Override
  public void shutdown() {}

  public ProviderMethod getMethod() {
    return this.method;
  }

  public static Path getServerPack() {
    return SERVER_PACK;
  }

  public String getFinalUrl() {
    if (this.url == null) {
      this.url = this.getRawUrl();
    }
    return this.url;
  }
}
