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
package me.brandonli.murderrun.game.map.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameMode;
import me.brandonli.murderrun.game.freezetag.FreezeTagManager;
import me.brandonli.murderrun.game.freezetag.FreezeTagReviveEvent;
import me.brandonli.murderrun.game.map.GameMap;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public final class GameEventManager {

  private final Game game;
  private final Collection<GameEvent> events;

  public GameEventManager(final GameMap map) {
    this.game = map.getGame();
    final List<GameEvent> eventList = new ArrayList<>();
    final FreezeTagManager manager = this.game.getFreezeTagManager();
    eventList.add(new GamePlayerDeathEvent(this.game, manager));
    eventList.add(new GamePlayerPickupEvent(this.game));
    eventList.add(new GamePlayerThrowEvent(this.game));
    eventList.add(new GamePlayerLeaveEvent(this.game));
    eventList.add(new GamePlayerHungerEvent(this.game));
    eventList.add(new GamePlayerRegenEvent(this.game));
    eventList.add(new GamePlayerBlockEvent(this.game));
    eventList.add(new GameMobSpawnEvent(this.game));
    eventList.add(new GamePlayerDismountEvent(this.game));
    eventList.add(new GamePlayerTeleportEvent(this.game));
    eventList.add(new GamePlayerChatEvent(this.game));
    eventList.add(new GamePlayerBlockBlackList(this.game));
    eventList.add(new GameBlockBreakEvent(this.game));
    eventList.add(new GameEntityDeathEvent(this.game));
    eventList.add(new GamePlayerArmorEvent(this.game));
    eventList.add(new GamePlayerClickEvent(this.game));

    final GameMode mode = this.game.getMode();
    if (mode == GameMode.FREEZE_TAG) {
      eventList.add(new FreezeTagReviveEvent(this.game));
    }

    this.events = eventList;
  }

  public Game getGame() {
    return this.game;
  }

  public Collection<GameEvent> getEvents() {
    return this.events;
  }

  public void registerEvents() {
    final MurderRun plugin = this.game.getPlugin();
    final Server server = plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    for (final GameEvent listener : this.events) {
      manager.registerEvents(listener, plugin);
    }
  }

  public void unregisterEvents() {
    for (final GameEvent listener : this.events) {
      HandlerList.unregisterAll(listener);
    }
  }
}
