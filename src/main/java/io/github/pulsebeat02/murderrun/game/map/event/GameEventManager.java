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
package io.github.pulsebeat02.murderrun.game.map.event;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.map.Map;
import java.util.Collection;
import java.util.Set;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public final class GameEventManager {

  private final Game game;
  private final Collection<GameEvent> events;

  public GameEventManager(final Map map) {
    this.game = map.getGame();
    this.events = Set.of(
      new GamePlayerDeathEvent(this.game),
      new GamePlayerPickupEvent(this.game),
      new GamePlayerThrowCarPartEvent(this.game),
      new GamePlayerLeaveEvent(this.game),
      new GamePlayerHungerEvent(this.game),
      new GamePlayerRegenEvent(this.game),
      new GamePlayerBlockEvent(this.game),
      new GameMobSpawnEvent(this.game),
      new GamePlayerDismountEvent(this.game),
      new GamePlayerTeleportEvent(this.game),
      new GamePlayerChatEvent(this.game),
      new GamePlayerBlockBlackList(this.game)
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
