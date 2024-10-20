package io.github.pulsebeat02.murderrun.resourcepack.provider;

import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.PackWrapper;
import io.github.pulsebeat02.murderrun.utils.ExecutorUtils;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;

public abstract class ResourcePackProvider implements PackProvider {

  private static final Path SERVER_PACK;

  static {
    try {
      SERVER_PACK = IOUtils.createTemporaryPath("murder-run-pack", ".zip");
      final PackWrapper wrapper = new PackWrapper(SERVER_PACK);
      wrapper.wrapPack();
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private final ProviderMethod method;
  private final ExecutorService service;

  private String url;

  public ResourcePackProvider(final ProviderMethod method) {
    this.method = method;
    this.service = Executors.newVirtualThreadPerTaskExecutor();
  }

  public abstract String getRawUrl(final Path zip);

  @Override
  public CompletableFuture<ResourcePackRequest> getResourcePackRequest() {
    return CompletableFuture.supplyAsync(() -> {
      final Component message = Message.RESOURCEPACK_PROMPT.build();
      final CompletableFuture<ResourcePackInfo> main = this.getMainResourceInfo();
      final CompletableFuture<ResourcePackInfo> builtIn = this.getResourceInfo();
      final Collection<ResourcePackInfo> infos = Set.of(main.join(), builtIn.join());
      final ResourcePackRequest.Builder builder = ResourcePackRequest.resourcePackRequest();
      return builder.required(true).packs(infos).prompt(message).replace(true).asResourcePackRequest();
    }, this.service);
  }

  private CompletableFuture<ResourcePackInfo> getMainResourceInfo() {
    final String url = this.getFinalUrl();
    final URI uri = URI.create(url);
    return ResourcePackInfo.resourcePackInfo().uri(uri).computeHashAndBuild(this.service);
  }

  private CompletableFuture<ResourcePackInfo> getResourceInfo() {
    final String url = GameProperties.BUILT_IN_RESOURCES;
    final URI uri = URI.create(url);
    return ResourcePackInfo.resourcePackInfo().uri(uri).computeHashAndBuild(this.service);
  }

  @Override
  public void start() {}

  @Override
  public void shutdown() {
    ExecutorUtils.shutdownExecutorGracefully(this.service);
  }

  public ProviderMethod getMethod() {
    return this.method;
  }

  public String getFinalUrl() {
    if (this.url == null) {
      this.url = this.getRawUrl(SERVER_PACK);
    }
    return this.url;
  }
}
