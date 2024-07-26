package io.github.pulsebeat02.murderrun.map;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.map.event.GameEventManager;
import io.github.pulsebeat02.murderrun.map.part.CarPartManager;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class MurderMap {

  private final MurderGame game;
  private final CarPartManager carPartManager;
  private final GameEventManager eventManager;
  private final MurderMapResetManager resetManager;

  public MurderMap(final MurderGame game) {
    this.game = game;
    this.carPartManager = (@Initialized CarPartManager) new CarPartManager(this);
    this.eventManager = (@Initialized GameEventManager) new GameEventManager(this);
    this.resetManager = (@Initialized MurderMapResetManager) new MurderMapResetManager(this);
  }

  public GameEventManager getEventManager() {
    return this.eventManager;
  }

  public MurderMapResetManager getResetManager() {
    return this.resetManager;
  }

  public void start() {
    this.resetMap();
    this.registerEvents();
    this.spawnParts();
  }

  private void resetMap() {}

  private void registerEvents() {
    this.eventManager.registerEvents();
  }

  private void spawnParts() {
    this.carPartManager.spawnParts();
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
    this.carPartManager.shutdownExecutor();
  }

  private void resetWorld() {
    this.resetManager.resetMap();
  }

  public MurderGame getGame() {
    return this.game;
  }

  public CarPartManager getCarPartManager() {
    return this.carPartManager;
  }
}
