package io.github.pulsebeat02.murderrun.reflect;

import org.bukkit.Bukkit;

public final class NMSHandler {

  public static NMSUtils NMS_UTILS;
  private static final String VERSION;

  static {
    final String ver = Bukkit.getServer().getBukkitVersion();
    final String mcVer = ver.split("-")[0];
    final String complete = String.format("v%s", mcVer);
    VERSION = complete.replace(".", "_");
    try {
      NMS_UTILS = getNMSUtils();
    } catch (final Exception e) {
      throw new AssertionError(e);
    }
  }

  private static NMSUtils getNMSUtils() throws Exception {
    return (NMSUtils) getNMSUtilsClass().getDeclaredConstructor().newInstance();
  }

  private static Class<?> getNMSUtilsClass() throws ClassNotFoundException {
    return Class.forName("io.github.pulsebeat02.murderrun.reflect.%s.NMSImpl".formatted(VERSION));
  }

  public static String getVersion() {
    return VERSION;
  }

  public static void init() {}
}
