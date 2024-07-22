package io.github.pulsebeat02.murderrun.map;

import io.github.pulsebeat02.murderrun.MurderGame;
import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.map.part.CarPartManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public final class MurderMap {

  private final MurderGame game;
  private final CarPartManager carPartManager;
  private MapEvents event;

  public MurderMap(final MurderGame game) {
    this.game = game;
    this.carPartManager = new CarPartManager(this);
  }

  public void start() {
    this.resetMap();
    this.registerEvents();
    this.spawnParts();
  }

  public void stop() {
    this.unregisterEvents();
  }

  private void unregisterEvents() {
    HandlerList.unregisterAll(this.event);
  }

  private void resetMap() {}

  private void spawnParts() {
    this.carPartManager.spawnParts();
  }

  private void registerEvents() {
    final MurderRun run = this.game.getPlugin();
    this.event = new MapEvents(this.game);
    Bukkit.getPluginManager().registerEvents(this.event, run);
  }

  public MurderGame getGame() {
    return this.game;
  }

  public CarPartManager getCarPartManager() {
    return this.carPartManager;
  }
}
