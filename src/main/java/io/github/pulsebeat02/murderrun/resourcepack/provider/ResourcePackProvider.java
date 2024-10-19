package io.github.pulsebeat02.murderrun.resourcepack.provider;

import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.PackWrapper;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

  private String url;

  public ResourcePackProvider(final ProviderMethod method) {
    this.method = method;
  }

  public abstract String getRawUrl(final Path zip);

  @Override
  public CompletableFuture<ResourcePackRequest> getResourcePackRequest() {
    final String url = this.getFinalUrl();
    final URI uri = URI.create(url);
    final CompletableFuture<ResourcePackInfo> info = ResourcePackInfo.resourcePackInfo().uri(uri).computeHashAndBuild();
    final CompletableFuture<ResourcePackInfo> builtIn = this.getResourceInfo();
    return this.constructRequest(info, builtIn);
  }

  @SafeVarargs
  private CompletableFuture<ResourcePackRequest> constructRequest(final CompletableFuture<ResourcePackInfo>... infos) {
    final CompletableFuture<Void> ignoredCf = CompletableFuture.allOf(infos);
    return ignoredCf.thenApply(ignored -> {
      final ResourcePackRequest.Builder builder = ResourcePackRequest.resourcePackRequest();
      final List<ResourcePackInfo> packInfos = new ArrayList<>();
      for (final CompletableFuture<ResourcePackInfo> info : infos) {
        final ResourcePackInfo packInfo = info.join();
        packInfos.add(packInfo);
      }
      final Component message = Message.RESOURCEPACK_PROMPT.build();
      return builder.required(true).packs(packInfos).prompt(message).replace(true).asResourcePackRequest();
    });
  }

  private CompletableFuture<ResourcePackInfo> getResourceInfo() {
    final String url = GameProperties.BUILT_IN_RESOURCES;
    final URI uri = URI.create(url);
    return ResourcePackInfo.resourcePackInfo().uri(uri).computeHashAndBuild();
  }

  @Override
  public void start() {}

  @Override
  public void shutdown() {}

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
