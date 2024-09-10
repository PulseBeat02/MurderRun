package io.github.pulsebeat02.murderrun.game.statistics;

import io.github.pulsebeat02.murderrun.data.json.AbstractJSONDataManager;

public final class StatisticJSONMapper extends AbstractJSONDataManager<StatisticsManager> {

  public StatisticJSONMapper() {
    super("player-statistics.json");
  }
}
