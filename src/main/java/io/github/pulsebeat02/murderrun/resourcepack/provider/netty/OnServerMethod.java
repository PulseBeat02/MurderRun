package io.github.pulsebeat02.murderrun.resourcepack.provider.netty;

import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ProviderMethod;
import io.github.pulsebeat02.murderrun.resourcepack.provider.ResourcePackProvider;
import java.nio.file.Path;

public final class OnServerMethod extends ResourcePackProvider {

  private ResourcePackHandler handler;

  public OnServerMethod() {
    super(ProviderMethod.ON_SERVER);
  }

  @Override
  public String getRawUrl(final Path zip) {

    if (this.handler == null) {
      this.handler = new ResourcePackHandler(zip);
      PacketToolsProvider.PACKET_API.injectNettyHandler(this.handler);
    }

    return "";
  }
}
