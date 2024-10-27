package io.github.pulsebeat02.murderrun.game.lobby;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.*;
import io.github.pulsebeat02.murderrun.game.event.PreGameEvents;
import java.util.Collection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PreGameManager {

  private final MurderRun plugin;
  private final Game game;
  private final GameSettings settings;
  private final String id;
  private final GameEventsListener callback;

  private PreGamePlayerManager manager;
  private PreGameEvents events;

  public PreGameManager(final MurderRun plugin, final String id, final GameEventsListener callback) {
    this.plugin = plugin;
    this.callback = callback;
    this.id = id;
    this.game = new Game(plugin);
    this.settings = new GameSettings();
  }

  public void initialize(final CommandSender leader, final int min, final int max, final boolean quickJoinable) {
    this.manager = new PreGamePlayerManager(this, leader, min, max, quickJoinable);
    this.events = new PreGameEvents(this);
    this.events.registerEvents();
    this.manager.initialize();
  }

  public void startGame() {
    final Collection<Player> players = this.manager.getParticipants();
    final Collection<Player> killers = this.manager.getMurderers();
    this.manager.assignKiller();
    this.game.startGame(this.settings, killers, players, this.callback);
    this.shutdown();
  }

  public void shutdown() {
    this.events.unregisterEvents();
    this.manager.shutdown();
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public Game getGame() {
    return this.game;
  }

  public GameSettings getSettings() {
    return this.settings;
  }

  public String getId() {
    return this.id;
  }

  public PreGamePlayerManager getPlayerManager() {
    return this.manager;
  }

  public GameEventsListener getCallback() {
    return this.callback;
  }

  public PreGameEvents getEvents() {
    return this.events;
  }
}
