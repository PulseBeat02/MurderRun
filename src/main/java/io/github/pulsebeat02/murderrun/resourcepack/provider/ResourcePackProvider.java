package io.github.pulsebeat02.murderrun.resourcepack.provider;

import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.ServerResourcepack;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;

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

  private String url;

  public ResourcePackProvider(final ProviderMethod method) {
    this.method = method;
    this.zip = SERVER_PACK.getPath();
  }

  abstract String getRawUrl(final Path zip);

  @Override
  public CompletableFuture<ResourcePackRequest> getResourcePackRequest() {
    final String url = this.getFinalUrl();
    final byte[] raw = createHashSha1(this.zip);
    final URI uri = URI.create(url);
    final Component message = Message.RESOURCEPACK_PROMPT.build();
    final CompletableFuture<ResourcePackInfo> info =
        ResourcePackInfo.resourcePackInfo().uri(uri).computeHashAndBuild();
    return info.thenApplyAsync(pack -> ResourcePackRequest.resourcePackRequest()
        .packs(pack)
        .required(true)
        .prompt(message)
        .replace(true)
        .asResourcePackRequest());
  }

  public static byte[] createHashSha1(final Path file) {
    try {
      final MessageDigest digest = MessageDigest.getInstance("SHA-1");
      final InputStream fis = new FileInputStream(file.toFile());
      int n = 0;
      final byte[] buffer = new byte[8192];
      while (n != -1) {
        n = fis.read(buffer);
        if (n > 0) {
          digest.update(buffer, 0, n);
        }
      }
      return digest.digest();
    } catch (final IOException | NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public void start() {}

  @Override
  public void shutdown() {}

  public ProviderMethod getMethod() {
    return this.method;
  }

  public Path getZip() {
    return this.zip;
  }

  public String getFinalUrl() {
    if (this.url == null) {
      this.url = this.getRawUrl(this.zip);
    }
    return this.url;
  }
}
