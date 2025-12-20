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
package me.brandonli.murderrun.game.capability;

public final class Capabilities {

  private Capabilities() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Capability LIBSDISGUISES = new PluginCapability("LibsDisguises");
  public static Capability PLACEHOLDERAPI = new PluginCapability("PlaceholderAPI");
  public static Capability FASTASYNCWORLDEDIT = new PluginClassCapability("com.fastasyncworldedit.bukkit.FaweBukkit");
  public static Capability PARTIES = new PluginCapability("Parties");
  public static Capability NEXO = new PluginCapability("Nexo");
  public static Capability CRAFTENGINE = new PluginCapability("CraftEngine");
  public static Capability VAULT = new PluginCapability("Vault");
}
