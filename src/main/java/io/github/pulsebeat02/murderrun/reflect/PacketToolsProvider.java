package io.github.pulsebeat02.murderrun.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public final class PacketToolsProvider {

  private static final String CLASS_PATH = "io.github.pulsebeat02.murderrun.reflect.%s.PacketTools";

  public static final PacketToolAPI INSTANCE;

  static {
    final Server server =
        Bukkit.getServer(); // only supporting latest version for each major release
    final String bukkitVersion = server.getBukkitVersion(); // 1.21-R0.1-SNAPSHOT
    final String minecraftVersion = bukkitVersion.split("-")[0]; // 1.21
    final String packageVersion = "v%s".formatted(minecraftVersion); // v1.21
    final String version = packageVersion.replace(".", "_"); // v1_21
    PacketToolAPI api;
    try {
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
    INSTANCE = api;
  }

  public static void init() {
    // instantiate packet api singleton
  }
}
