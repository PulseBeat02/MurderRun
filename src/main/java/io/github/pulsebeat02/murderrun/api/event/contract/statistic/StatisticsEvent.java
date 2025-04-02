/*

MIT License

Copyright (c) 2025 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.api.event.contract.statistic;

import io.github.pulsebeat02.murderrun.api.event.Cancellable;
import io.github.pulsebeat02.murderrun.api.event.MurderRunEvent;

public interface StatisticsEvent<T extends Number> extends MurderRunEvent, Cancellable {
  T getChange();

  StatisticsType<T> getStatisticsType();

  class StatisticsType<T extends Number> {

    public static final StatisticsType<Long> FASTEST_KILLER_WIN = of("FASTEST_KILLER_WIN", Long.TYPE);
    public static final StatisticsType<Long> FASTEST_SURVIVOR_WIN = of("FASTEST_SURVIVOR_WIN", Long.TYPE);
    public static final StatisticsType<Integer> TOTAL_KILLS = of("TOTAL_KILLS", Integer.TYPE);
    public static final StatisticsType<Integer> TOTAL_DEATHS = of("TOTAL_DEATHS", Integer.TYPE);
    public static final StatisticsType<Integer> TOTAL_WINS = of("TOTAL_WINS", Integer.TYPE);
    public static final StatisticsType<Integer> TOTAL_LOSSES = of("TOTAL_LOSSES", Integer.TYPE);
    public static final StatisticsType<Integer> TOTAL_GAMES = of("TOTAL_GAMES", Integer.TYPE);
    public static final StatisticsType<Float> WIN_LOSS_RATIO = of("WIN_LOSS_RATIO", Float.TYPE);

    private static <T extends Number> StatisticsType<T> of(final String name, final Class<T> clazz) {
      return new StatisticsType<>(name, clazz);
    }

    private final String name;
    private final Class<T> clazz;

    private StatisticsType(final String name, final Class<T> clazz) {
      this.name = name;
      this.clazz = clazz;
    }

    public Class<T> getType() {
      return this.clazz;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }
}
