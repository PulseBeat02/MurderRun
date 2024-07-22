package io.github.pulsebeat02.murderrun.map.event;

import io.github.pulsebeat02.murderrun.MurderGame;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.map.MurderMap;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.Collection;
import java.util.Set;

public final class GameEventManager {

  private final MurderGame game;
  private final Collection<Listener> events;

  public GameEventManager(final MurderMap map) {
    this.game = map.getGame();
    this.events =
        Set.of(
            new GamePlayerDeathEvent(this.game),
            new GamePlayerPickupCarPartEvent(this.game),
            new GamePlayerThrowCarPartEvent(this.game),
            new GamePlayerLeaveEvent(this.game),
            new GamePlayerHungerEvent(this.game));
  }

  public void registerEvents() {
    final MurderRun plugin = this.game.getPlugin();
    final Server server = plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    for (final Listener listener : this.events) {
      manager.registerEvents(listener, plugin);
    }
  }

  public void unregisterEvents() {
    for (final Listener listener : this.events) {
      HandlerList.unregisterAll(listener);
    }
  }
}
