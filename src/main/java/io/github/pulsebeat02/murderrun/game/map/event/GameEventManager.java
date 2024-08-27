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
        new GamePlayerDismountEvent(this.game));
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
