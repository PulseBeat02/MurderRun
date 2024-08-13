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
    final Server server = Bukkit.getServer();
    final String bukkitVersion = server.getBukkitVersion();
    final String minecraftVersion = bukkitVersion.split("-")[0];
    final String packageVersion = String.format("v%s", minecraftVersion);
    final String version = packageVersion.replace(".", "_");
    PacketToolAPI api;
    try {
      final String path = String.format(CLASS_PATH, version);
      final Class<?> clazz = Class.forName(path);
      final MethodHandles.Lookup lookup = MethodHandles.lookup();
      final MethodType type = MethodType.methodType(Void.TYPE);
      final MethodHandle handle = lookup.findConstructor(clazz, type);
      api = (PacketToolAPI) handle.invoke();
    } catch (final Throwable e) {
      api = new FallbackPacketTools();
      throw new IllegalStateException(
          "The current server version isn't supported by this plugin! Resorting to fallback adapter",
          e);
    }
    INSTANCE = api;
  }

  public static void init() {}
}
