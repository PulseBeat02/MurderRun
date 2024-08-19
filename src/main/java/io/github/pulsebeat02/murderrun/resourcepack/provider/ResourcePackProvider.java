package io.github.pulsebeat02.murderrun.resourcepack.provider;

import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.ServerResourcepack;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public abstract class ResourcePackProvider implements PackProvider {

  private static final ServerResourcepack SERVER_PACK;

  static {
    SERVER_PACK = new ServerResourcepack();
    try {
      SERVER_PACK.build();
    } catch (final IOException e) {
      throw new AssertionError(e);
    }
  }

  private final ProviderMethod method;
  private final Path zip;
  private final String url;
  private final String hash;

  public ResourcePackProvider(final ProviderMethod method) {
    this.method = method;
    this.zip = SERVER_PACK.getPath();
    this.hash = this.getRawHash(this.zip);
    this.url = this.getRawUrl();
  }

  abstract String getRawUrl(@UnderInitialization ResourcePackProvider this);

  private String getRawHash(@UnderInitialization ResourcePackProvider this, final Path zip) {
    try {
      return IOUtils.generateFileHash(zip);
    } catch (final IOException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ResourcePackRequest getResourcePackRequest() {
    final URI uri = URI.create(this.url);
    final UUID id = UUID.randomUUID();
    final Component message = Message.RESOURCEPACK_PROMPT.build();
    final ResourcePackInfo info = ResourcePackInfo.resourcePackInfo(id, uri, this.hash);
    return ResourcePackRequest.resourcePackRequest()
        .packs(info)
        .required(true)
        .prompt(message)
        .asResourcePackRequest();
  }

  @Override
  public void start() {}

  @Override
  public void shutdown() {}

  public ProviderMethod getMethod() {
    return this.method;
  }

  public String getUrl() {
    return this.url;
  }

  public String getHash() {
    return this.hash;
  }

  public Path getZip() {
    return this.zip;
  }
}
