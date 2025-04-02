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
package me.brandonli.murderrun.api.event;

import me.brandonli.murderrun.MurderRun;
import org.bukkit.plugin.java.JavaPlugin;

public final class EventBusProvider {

  private static final ApiEventBus EVENT_BUS;

  static {
    final MurderRun plugin = (MurderRun) JavaPlugin.getProvidingPlugin(MurderRun.class);
    EVENT_BUS = new ApiEventBus(plugin);
  }

  public static void init() {
    // init events
  }

  private EventBusProvider() {}

  public static ApiEventBus getBus() {
    return EVENT_BUS;
  }
}
