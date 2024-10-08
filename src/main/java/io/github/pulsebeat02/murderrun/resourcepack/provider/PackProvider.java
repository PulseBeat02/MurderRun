package io.github.pulsebeat02.murderrun.resourcepack.provider;

import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.resource.ResourcePackRequest;

public interface PackProvider {
  CompletableFuture<ResourcePackRequest> getResourcePackRequest();

  void start();

  void shutdown();
}
