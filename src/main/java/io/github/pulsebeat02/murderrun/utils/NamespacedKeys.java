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
  public static NamespacedKey KILLER_TRACKER;
  public static NamespacedKey FLASH_BANG;
  public static NamespacedKey SMOKE_GRENADE;
  public static NamespacedKey FLASH_LIGHT_LAST_USE;
  public static NamespacedKey TRANSLOCATOR;

  public static void init(final MurderRun plugin) {
    PLUGIN = plugin;
    SPECIAL_SWORD = createKey("sword");
    CAR_PART_UUID = createKey("car-part-uuid");
    TRAP_KEY_NAME = createKey("trap");
    CAN_BREAK_BLOCKS = createKey("can-break-blocks");
    KILLER_TRACKER = createKey("killer-tracker");
    FLASH_BANG = createKey("flash-bang");
    SMOKE_GRENADE = createKey("smoke-grenade");
    FLASH_LIGHT_LAST_USE = createKey("flash-light-last-use");
    TRANSLOCATOR = createKey("translocator");
  }

  private static NamespacedKey createKey(final String key) {
    return new NamespacedKey(PLUGIN, key);
  }
}
