package io.github.pulsebeat02.murderrun.game.papi;

import io.github.pulsebeat02.murderrun.game.statistics.PlayerStatistics;
import java.util.function.Function;

public final class PAPIPlaceholder {

  private final String name;
  private final Function<PlayerStatistics, Object> function;

  public PAPIPlaceholder(final String name, final Function<PlayerStatistics, Object> function) {
    this.name = name;
    this.function = function;
  }

  public String getStatistic(final PlayerStatistics statistics) {
    return String.valueOf(this.function.apply(statistics));
  }

  public String getName() {
    return this.name;
  }
}
