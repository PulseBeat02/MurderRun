package io.github.pulsebeat02.murderrun.game.papi;

import io.github.pulsebeat02.murderrun.game.statistics.PlayerStatistics;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

public final class PlaceholderHandler {

  private static final Collection<Placeholder> PLACEHOLDERS = new HashSet<>();

  private static final Placeholder FASTEST_WIN_KILLER = of("fastest_win_killer", checkValue(PlayerStatistics::getFastestWinKiller));
  private static final Placeholder FASTEST_WIN_SURVIVOR = of("fastest_win_survivor", checkValue(PlayerStatistics::getFastestWinSurvivor));
  private static final Placeholder TOTAL_KILLS = of("total_kills", PlayerStatistics::getTotalKills);
  private static final Placeholder TOTAL_DEATHS = of("total_deaths", PlayerStatistics::getTotalDeaths);
  private static final Placeholder TOTAL_WINS = of("total_wins", PlayerStatistics::getTotalWins);
  private static final Placeholder TOTAL_LOSSES = of("total_losses", PlayerStatistics::getTotalLosses);
  private static final Placeholder TOTAL_GAMES = of("total_games", PlayerStatistics::getTotalGames);
  private static final Placeholder WIN_LOSS_RATIO = of("win_loss_ratio", PlayerStatistics::getWinLossRatio);

  private static Function<PlayerStatistics, Object> checkValue(final Function<PlayerStatistics, Long> function) {
    return statistics -> {
      final long value = function.apply(statistics);
      if (value == Long.MAX_VALUE) {
        return "N/A";
      }
      return value;
    };
  }

  private static Placeholder of(final String name, final Function<PlayerStatistics, Object> function) {
    final Placeholder holder = new Placeholder(name, function);
    PLACEHOLDERS.add(holder);
    return holder;
  }

  public @Nullable String getPlaceholder(final PlayerStatistics statistics, final String target) {
    for (final Placeholder placeholder : PLACEHOLDERS) {
      final String name = placeholder.getName();
      if (name.equalsIgnoreCase(target)) {
        return placeholder.getStatistic(statistics);
      }
    }
    return null;
  }
}
