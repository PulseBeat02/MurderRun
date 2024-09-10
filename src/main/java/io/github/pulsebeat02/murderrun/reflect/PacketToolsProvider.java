package io.github.pulsebeat02.murderrun.reflect;

import io.github.pulsebeat02.murderrun.MurderRun;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class PacketToolsProvider {

  private static final String CLASS_PATH = "io.github.pulsebeat02.murderrun.reflect.%s.PacketTools";

  private static final String VERSION;
  public static final PacketToolAPI PACKET_API;

  static {
    final Plugin plugin = JavaPlugin.getProvidingPlugin(MurderRun.class);
    final Server server =
        plugin.getServer(); // only supporting latest version for each major release
    final String bukkitVersion = server.getBukkitVersion(); // 1.21-R0.1-SNAPSHOT
    VERSION = bukkitVersion.split("-")[0]; // 1.21

    final String packageVersion = "v%s".formatted(VERSION); // v1.21
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
    PACKET_API = api;
  }

  public static String getVersion() {
    return VERSION;
  }

  public static void init() {
    // instantiate packet api singleton
  }
}
