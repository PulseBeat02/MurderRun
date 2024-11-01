package io.github.pulsebeat02.murderrun.game.papi;

import io.github.pulsebeat02.murderrun.game.statistics.PlayerStatistics;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

public final class PAPIPlaceholderParser {

  private static final Collection<PAPIPlaceholder> PLACEHOLDERS = new HashSet<>();

  private static final PAPIPlaceholder FASTEST_WIN_KILLER = of("fastest_win_killer", checkValue(PlayerStatistics::getFastestWinKiller));
  private static final PAPIPlaceholder FASTEST_WIN_SURVIVOR = of(
    "fastest_win_survivor",
    checkValue(PlayerStatistics::getFastestWinSurvivor)
  );
  private static final PAPIPlaceholder TOTAL_KILLS = of("total_kills", PlayerStatistics::getTotalKills);
  private static final PAPIPlaceholder TOTAL_DEATHS = of("total_deaths", PlayerStatistics::getTotalDeaths);
  private static final PAPIPlaceholder TOTAL_WINS = of("total_wins", PlayerStatistics::getTotalWins);
  private static final PAPIPlaceholder TOTAL_LOSSES = of("total_losses", PlayerStatistics::getTotalLosses);
  private static final PAPIPlaceholder TOTAL_GAMES = of("total_games", PlayerStatistics::getTotalGames);
  private static final PAPIPlaceholder WIN_LOSS_RATIO = of("win_loss_ratio", PlayerStatistics::getWinLossRatio);

  private static Function<PlayerStatistics, Object> checkValue(final Function<PlayerStatistics, Long> function) {
    return statistics -> {
      final long value = function.apply(statistics);
      if (value == Long.MAX_VALUE) {
        return "N/A";
      }
      return value;
    };
  }

  private static PAPIPlaceholder of(final String name, final Function<PlayerStatistics, Object> function) {
    final PAPIPlaceholder holder = new PAPIPlaceholder(name, function);
    PLACEHOLDERS.add(holder);
    return holder;
  }

  public @Nullable String getPlaceholder(final PlayerStatistics statistics, final String target) {
    for (final PAPIPlaceholder placeholder : PLACEHOLDERS) {
      final String name = placeholder.getName();
      if (name.equalsIgnoreCase(target)) {
        return placeholder.getStatistic(statistics);
      }
    }
    return null;
  }
}
