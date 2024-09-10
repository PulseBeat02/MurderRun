package io.github.pulsebeat02.murderrun.reflect;

import io.github.pulsebeat02.murderrun.reflect.versioning.ServerEnvironment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class PacketToolsProvider {

  private static final String CLASS_PATH = "io.github.pulsebeat02.murderrun.reflect.%s.PacketTools";

  public static final PacketToolAPI PACKET_API;

  static {
    PacketToolAPI api;
    try {
      final String version = ServerEnvironment.getNMSRevision();
      final String path = CLASS_PATH.formatted(version);
      final Class<?> clazz = Class.forName(path);
      final MethodHandles.Lookup lookup = MethodHandles.lookup();
      final MethodType type = MethodType.methodType(Void.TYPE);
      final MethodHandle handle = lookup.findConstructor(clazz, type);
      api = (PacketToolAPI) handle.invoke();
    } catch (final Throwable e) {
      api = new FallbackPacketTools();
      throw new UnsupportedOperationException(
          "The current server version isn't supported by this plugin! Resorting to fallback adapter",
          e);
    }
    PACKET_API = api;
  }

  public static void init() {
    // instantiate packet api singleton
  }
}
