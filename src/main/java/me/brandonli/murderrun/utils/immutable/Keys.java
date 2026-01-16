/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.utils.immutable;

import org.bukkit.NamespacedKey;

public final class Keys {

  private Keys() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static final String NAMESPACE = "murderrun";

  public static NamespacedKey LEAVE = createNamespacedPluginKey("leave");
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
  public static NamespacedKey FLASHLIGHT_COOLDOWN = createNamespacedPluginKey("flashlight_cooldown");
  public static NamespacedKey FLASHLIGHT = createNamespacedPluginKey("flashlight");

  private static NamespacedKey createNamespacedPluginKey(final String key) {
    return new NamespacedKey(NAMESPACE, key);
  }
}
