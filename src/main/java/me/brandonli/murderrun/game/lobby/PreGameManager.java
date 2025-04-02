/*

MIT License

Copyright (c) 2025 Brandon Li

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
package me.brandonli.murderrun.game.lobby;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameEventsListener;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.lobby.event.PreGameEvents;
import me.brandonli.murderrun.game.map.MapSchematicIO;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PreGameManager {

  private final MurderRun plugin;
  private final Game game;
  private final GameSettings settings;
  private final String id;
  private final GameEventsListener callback;
  private final GameManager gameManager;
  private final UUID uuid;

  private MapSchematicIO mapSchematicIO;
  private PreGamePlayerManager manager;
  private PreGameEvents events;

  public PreGameManager(final MurderRun plugin, final GameManager manager, final String id, final GameEventsListener callback) {
    this.plugin = plugin;
    this.gameManager = manager;
    this.callback = callback;
    this.id = id;
    this.game = new Game(plugin);
    this.settings = new GameSettings();
    this.uuid = UUID.randomUUID();
  }

  public CompletableFuture<Void> initialize(final CommandSender leader, final int min, final int max, final boolean quickJoinable) {
    this.manager = new PreGamePlayerManager(this, leader, min, max, quickJoinable);
    this.events = new PreGameEvents(this);
    this.mapSchematicIO = new MapSchematicIO(this.settings, this.uuid);
    this.events.registerEvents();
    this.manager.initialize();
    return this.mapSchematicIO.pasteMap();
  }

  public void startGame() {
    final Collection<Player> players = this.manager.getParticipants();
    final Collection<Player> killers = this.manager.getMurderers();
    this.manager.assignKiller();
    this.game.startGame(this.settings, killers, players, this.callback, this.mapSchematicIO, this.uuid);
    this.shutdown(false);
  }

  public void shutdown(final boolean forced) {
    final AtomicBoolean disabling = this.plugin.isDisabling();
    this.events.unregisterEvents();
    this.manager.shutdown();
    if (disabling.get()) {
      this.mapSchematicIO.resetMapShutdown();
    } else if (forced) {
      this.mapSchematicIO.resetMap();
    }
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

  public GameManager getGameManager() {
    return this.gameManager;
  }

  public MapSchematicIO getMapSchematicIO() {
    return this.mapSchematicIO;
  }

  public UUID getUuid() {
    return this.uuid;
  }
}
