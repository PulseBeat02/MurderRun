package io.github.pulsebeat02.murderrun.game.statistics;

import io.github.pulsebeat02.murderrun.data.AbstractJSONDataManager;

public final class StatisticManagerJSONMapper extends AbstractJSONDataManager<StatisticsManager> {

  public StatisticManagerJSONMapper() {
    super(StatisticsManager.class, "player-statistics.json");
  }
}
