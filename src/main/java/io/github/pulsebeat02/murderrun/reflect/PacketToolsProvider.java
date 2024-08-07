package io.github.pulsebeat02.murderrun.reflect;

import org.bukkit.Bukkit;

public final class PacketToolsProvider {

  public static final PacketToolAPI INSTANCE;
  private static final String VERSION;

  static {
    final String ver = Bukkit.getServer().getBukkitVersion();
    final String mcVer = ver.split("-")[0];
    final String complete = String.format("v%s", mcVer);
    VERSION = complete.replace(".", "_");
    try {
      final String path =
          "io.github.pulsebeat02.murderrun.reflect.%s.PacketTools".formatted(VERSION);
      final Class<?> clazz = Class.forName(path);
      INSTANCE = (PacketToolAPI) clazz.getDeclaredConstructor().newInstance();
    } catch (final Exception e) {
      throw new AssertionError(e);
    }
  }

  public static String getVersion() {
    return VERSION;
  }

  public static void init() {}
}
