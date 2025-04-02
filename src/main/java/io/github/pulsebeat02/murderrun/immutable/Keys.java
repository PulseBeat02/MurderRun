/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.immutable;

import org.bukkit.NamespacedKey;

public final class Keys {

  private Keys() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static final String NAMESPACE = "murderrun";

  public static NamespacedKey SPECIAL_SWORD = createNamespacedPluginKey("sword");
  public static NamespacedKey CAR_PART_UUID = createNamespacedPluginKey("car-part-uuid");
  public static NamespacedKey GADGET_KEY_NAME = createNamespacedPluginKey("gadget");
  public static NamespacedKey ABILITY_KEY_NAME = createNamespacedPluginKey("ability");
  public static NamespacedKey CAN_BREAK_BLOCKS = createNamespacedPluginKey("can-break-blocks");
  public static NamespacedKey KILLER_TRACKER = createNamespacedPluginKey("killer-tracker");
  public static NamespacedKey FLASH_BANG = createNamespacedPluginKey("flash-bang");
  public static NamespacedKey SMOKE_GRENADE = createNamespacedPluginKey("smoke-grenade");
  public static NamespacedKey TRANSLOCATOR = createNamespacedPluginKey("translocator");
  public static NamespacedKey PLAYER_TRACKER = createNamespacedPluginKey("player-tracker");
  public static NamespacedKey HOOK = createNamespacedPluginKey("hook");
  public static NamespacedKey PORTAL_GUN = createNamespacedPluginKey("portal-gun");
  public static NamespacedKey UUID = createNamespacedPluginKey("uuid");
  public static NamespacedKey AIR_DROP = createNamespacedPluginKey("air_drop");
  public static NamespacedKey ICE_SPIRIT_OWNER = createNamespacedPluginKey("ice-spirit-owner");
  public static NamespacedKey DEATH_HOUND_OWNER = createNamespacedPluginKey("death-hound-owner");
  public static NamespacedKey DORMAGOGG_OWNER = createNamespacedPluginKey("dormagogg-owner");
  public static NamespacedKey ITEM_WAND = createNamespacedPluginKey("item-wand");
  public static NamespacedKey PLAYER_UUID = createNamespacedPluginKey("player-uuid");
  public static NamespacedKey ARENA_NAME = createNamespacedPluginKey("arena-name");
  public static NamespacedKey LOBBY_NAME = createNamespacedPluginKey("lobby-name");
  public static NamespacedKey KILLER_ROLE = createNamespacedPluginKey("killer-role");

  private static NamespacedKey createNamespacedPluginKey(final String key) {
    return new NamespacedKey(NAMESPACE, key);
  }
}
