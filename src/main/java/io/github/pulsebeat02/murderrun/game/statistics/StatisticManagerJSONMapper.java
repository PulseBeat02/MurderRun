package io.github.pulsebeat02.murderrun.game.statistics;

import io.github.pulsebeat02.murderrun.data.json.AbstractJSONDataManager;

public final class StatisticManagerJSONMapper extends AbstractJSONDataManager<StatisticsManager> {

  public StatisticManagerJSONMapper() {
    super("player-statistics.json");
  }
}
