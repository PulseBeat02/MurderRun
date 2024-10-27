package io.github.pulsebeat02.murderrun.game.event;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.lobby.PreGameManager;
import java.util.Collection;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public final class PreGameEvents {

  private final PreGameManager manager;
  private final Collection<Listener> events;

  public PreGameEvents(final PreGameManager manager) {
    this.manager = manager;
    this.events = Set.of(new DupePreventListener(manager), new DamagePreventionListener(manager), new PlayerLeaveListener(manager));
  }

  public void registerEvents() {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    final MurderRun plugin = this.manager.getPlugin();
    this.events.forEach(event -> manager.registerEvents(event, plugin));
  }

  public void unregisterEvents() {
    this.events.forEach(HandlerList::unregisterAll);
  }
}
