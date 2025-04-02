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

import java.util.Collection;
import java.util.Set;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.map.GameMap;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public final class GameEventManager {

  private final Game game;
  private final Collection<GameEvent> events;

  public GameEventManager(final GameMap map) {
    this.game = map.getGame();
    this.events = Set.of(
      new GamePlayerDeathEvent(this.game),
      new GamePlayerPickupEvent(this.game),
      new GamePlayerThrowEvent(this.game),
      new GamePlayerLeaveEvent(this.game),
      new GamePlayerHungerEvent(this.game),
      new GamePlayerRegenEvent(this.game),
      new GamePlayerBlockEvent(this.game),
      new GameMobSpawnEvent(this.game),
      new GamePlayerDismountEvent(this.game),
      new GamePlayerTeleportEvent(this.game),
      new GamePlayerChatEvent(this.game),
      new GamePlayerBlockBlackList(this.game),
      new GameBlockBreakEvent(this.game),
      new GameEntityDeathEvent(this.game),
      new GamePlayerArmorEvent(this.game),
      new GamePlayerClickEvent(this.game)
    );
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
