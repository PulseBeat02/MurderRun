package io.github.pulsebeat02.murderrun.map;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.utils.MapUtils;

public final class MapResetManager {

  private final MurderMap map;

  public MapResetManager(final MurderMap map) {
    this.map = map;
  }

  public void resetMap() {
    resetMapBlocksEntities();
  }

  public void killExistingEntities() {

  }

  public void resetMapBlocksEntities() {
    MapUtils.resetMap(map);
  }
}
