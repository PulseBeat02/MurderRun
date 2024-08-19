package io.github.pulsebeat02.murderrun.resourcepack.provider;

import net.kyori.adventure.resource.ResourcePackRequest;

public interface PackProvider {

  ResourcePackRequest getResourcePackRequest();

  void start();

  void shutdown();
}
