package io.github.pulsebeat02.murderrun.resourcepack.provider;

import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.PackWrapper;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
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
    final Component message = Message.RESOURCEPACK_PROMPT.build();
    final CompletableFuture<ResourcePackInfo> info = ResourcePackInfo.resourcePackInfo().uri(uri).computeHashAndBuild();
    return info.thenApplyAsync(pack ->
      ResourcePackRequest.resourcePackRequest()
        .packs(pack)
        .required(true)
        .prompt(message)
        .replace(true)
        .asResourcePackRequest()
    );
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
