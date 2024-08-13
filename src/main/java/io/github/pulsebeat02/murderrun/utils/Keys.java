package io.github.pulsebeat02.murderrun.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public final class Keys {

  private static final Plugin PLUGIN =
      CursedPluginInstanceRetrieverOnlyForUtilityClassesProvider.retrievePluginInstance();
  public static final String NAMESPACE = "murder_run";

  public static NamespacedKey SPECIAL_SWORD = createNamespacedPluginKey("sword");
  public static NamespacedKey CAR_PART_UUID = createNamespacedPluginKey("car-part-uuid");
  public static NamespacedKey GADGET_KEY_NAME = createNamespacedPluginKey("gadget");
  public static NamespacedKey CAN_BREAK_BLOCKS = createNamespacedPluginKey("can-break-blocks");
  public static NamespacedKey KILLER_TRACKER = createNamespacedPluginKey("killer-tracker");
  public static NamespacedKey FLASH_BANG = createNamespacedPluginKey("flash-bang");
  public static NamespacedKey SMOKE_GRENADE = createNamespacedPluginKey("smoke-grenade");
  public static NamespacedKey FLASH_LIGHT_LAST_USE =
      createNamespacedPluginKey("flash-light-last-use");
  public static NamespacedKey TRANSLOCATOR = createNamespacedPluginKey("translocator");

  private static NamespacedKey createNamespacedPluginKey(final String key) {
    return new NamespacedKey(PLUGIN, key);
  }
}
