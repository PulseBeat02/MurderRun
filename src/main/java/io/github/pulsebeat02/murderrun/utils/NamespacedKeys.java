package io.github.pulsebeat02.murderrun.utils;

import io.github.pulsebeat02.murderrun.MurderRun;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public final class NamespacedKeys {

  private static Plugin PLUGIN;
  public static NamespacedKey SPECIAL_SWORD;
  public static NamespacedKey CAR_PART_UUID;
  public static NamespacedKey TRAP_KEY_NAME;
  public static NamespacedKey CAN_BREAK_BLOCKS;

  public static void init(final MurderRun plugin) {
    PLUGIN = plugin;
    SPECIAL_SWORD = createKey("sword");
    CAR_PART_UUID = createKey("car-part-uuid");
    TRAP_KEY_NAME = createKey("trap");
    CAN_BREAK_BLOCKS = createKey("can-break-blocks");
  }

  private static NamespacedKey createKey(final String key) {
    return new NamespacedKey(PLUGIN, key);
  }
}
