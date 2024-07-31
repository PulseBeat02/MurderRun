package io.github.pulsebeat02.murderrun.immutable;

import io.github.pulsebeat02.murderrun.MurderRun;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public final class NamespacedKeys {

  public static final NamespacedKey SPECIAL_SWORD = createKey("sword");
  public static final NamespacedKey CAR_PART_UUID = createKey("car-part-uuid");
  public static final NamespacedKey TRAP_KEY_NAME = createKey("trap");
  public static final NamespacedKey CAN_BREAK_BLOCKS = createKey("can-break-blocks");

  private static Plugin PLUGIN;

  public static void init(final MurderRun plugin) {
    PLUGIN = plugin;
  }

  private static NamespacedKey createKey(final String key) {
    return new NamespacedKey(PLUGIN, key);
  }
}
