package io.github.pulsebeat02.murderrun.game;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import io.github.pulsebeat02.murderrun.MurderRun;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.PluginManager;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class PlayerResourcePackChecker implements Listener {

  private final MurderRun plugin;
  private final Set<Player> players;

  public PlayerResourcePackChecker(final MurderRun plugin) {
    this.players = Collections.newSetFromMap(new WeakHashMap<>());
    this.plugin = plugin;
  }

  public void registerEvents() {
    final Server server = Bukkit.getServer();
    final PluginManager manager = server.getPluginManager();
    manager.registerEvents(this, this.plugin);
  }

  @EventHandler
  public void onResourcePackLoad(final PlayerResourcePackStatusEvent event) {
    final Player player = event.getPlayer();
    final PlayerResourcePackStatusEvent.Status status = event.getStatus();
    if (status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
      this.markLoaded(player);
    }
  }

  public void markLoaded(final Player player) {
    this.players.add(player);
  }

  public boolean isLoaded(final Player player) {
    return this.players.contains(player);
  }
}
