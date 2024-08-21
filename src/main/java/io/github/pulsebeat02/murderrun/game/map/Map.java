package io.github.pulsebeat02.murderrun.game.map;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.map.event.GameEventManager;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;

public final class Map {

  private final Game game;

  private PartsManager partsManager;
  private GameEventManager eventManager;
  private MapResetTool resetManager;
  private TruckManager truckManager;
  private MapSchematicIO mapSchematicIO;

  public Map(final Game game) {
    this.game = game;
  }

  public void start() {
    this.partsManager = new PartsManager(this);
    this.eventManager = new GameEventManager(this);
    this.resetManager = new MapResetTool(this);
    this.truckManager = new TruckManager(this);
    this.mapSchematicIO = new MapSchematicIO(this);
    this.eventManager.registerEvents();
    this.truckManager.spawnParticles();
  }

  public GameEventManager getEventManager() {
    return this.eventManager;
  }

  public MapResetTool getResetManager() {
    return this.resetManager;
  }

  public void shutdown() {
    this.unregisterEvents();
    this.resetWorld();
  }

  private void unregisterEvents() {
    this.eventManager.unregisterEvents();
  }

  private void resetWorld() {
    this.resetManager.resetMap();
  }

  public MapSchematicIO getMapSchematicIO() {
    return this.mapSchematicIO;
  }

  public Game getGame() {
    return this.game;
  }

  public PartsManager getCarPartManager() {
    return this.partsManager;
  }

  public TruckManager getTruckManager() {
    return this.truckManager;
  }
}
