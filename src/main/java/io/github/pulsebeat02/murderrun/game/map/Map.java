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

  public Map(final Game game) {
    this.game = game;
  }

  public GameEventManager getEventManager() {
    return this.eventManager;
  }

  public MapResetTool getResetManager() {
    return this.resetManager;
  }

  public void start() {
    this.createManagers();
    this.registerEvents();
    this.spawnParts();
  }

  private void createManagers() {
    this.partsManager = new PartsManager(this);
    this.eventManager = new GameEventManager(this);
    this.resetManager = new MapResetTool(this);
    this.truckManager = new TruckManager(this);
  }

  private void registerEvents() {
    this.eventManager.registerEvents();
  }

  private void spawnParts() {
    this.partsManager.spawnParts();
    this.truckManager.spawnParticles();
  }

  public void shutdown() {
    this.unregisterEvents();
    this.stopExecutors();
    this.resetWorld();
  }

  private void unregisterEvents() {
    this.eventManager.unregisterEvents();
  }

  private void stopExecutors() {
    this.partsManager.shutdownExecutor();
    this.truckManager.shutdownExecutor();
  }

  private void resetWorld() {
    this.resetManager.resetMap();
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
