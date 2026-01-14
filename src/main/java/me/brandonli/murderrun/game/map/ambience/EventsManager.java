/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.map.ambience;

import java.util.Collection;
import java.util.Set;
import me.brandonli.murderrun.api.event.ApiEventBus;
import me.brandonli.murderrun.api.event.EventBusProvider;
import me.brandonli.murderrun.api.event.contract.event.RandomGameEvent;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.utils.RandomUtils;

public final class EventsManager {

  private static final Collection<RandomEvent> RANDOM_EVENTS = Set.of(
    new PotionEffectEvent(),
    new SmiteEvent(),
    new SoundEvent(),
    new WeatherEvent()
  );

  private static final int BASE_INTERVAL_SECONDS = 120;
  private static final int MIN_INTERVAL_SECONDS = 15;

  private final GameMap map;

  public EventsManager(final GameMap map) {
    this.map = map;
  }

  public void start() {
    final Game game = this.map.getGame();
    final GameScheduler scheduler = game.getScheduler();
    final GameProperties properties = game.getProperties();
    if (properties.isRandomEventsEnabled()) {
      final GamePlayerManager manager = game.getPlayerManager();
      final int count = manager.getTotalPlayers();
      this.scheduleRandomEventTasks(scheduler, count);
    }
  }

  private void scheduleRandomEventTasks(final GameScheduler scheduler, final int playerCount) {
    final int players = Math.max(1, playerCount);
    final int maxIntervalSeconds = Math.max(MIN_INTERVAL_SECONDS, BASE_INTERVAL_SECONDS / players);
    final NullReference reference = NullReference.of();
    final Game game = scheduler.getGame();
    final Runnable task = this.getRandomizationLogic(game, maxIntervalSeconds);
    final int initialIntervalSeconds = RandomUtils.generateInt(MIN_INTERVAL_SECONDS, maxIntervalSeconds + 1);
    final long ticks = initialIntervalSeconds * 20L;
    scheduler.scheduleTask(task, ticks, reference);
  }

  private Runnable getRandomizationLogic(final Game game, final int maxIntervalSeconds) {
    return new Runnable() {
      @Override
      public void run() {
        final RandomEvent event = RandomUtils.getRandomElement(RANDOM_EVENTS);
        if (EventsManager.this.invokeEvent(event)) {
          this.scheduleNextEvent(maxIntervalSeconds);
          return;
        }
        event.triggerEvent(game);
        this.scheduleNextEvent(maxIntervalSeconds);
      }

      private void scheduleNextEvent(final int maxIntervalSeconds) {
        final Game game = EventsManager.this.map.getGame();
        final GameScheduler scheduler = game.getScheduler();
        final int nextIntervalSeconds = RandomUtils.generateInt(MIN_INTERVAL_SECONDS, maxIntervalSeconds + 1);
        final long ticks = nextIntervalSeconds * 20L;
        final NullReference reference = NullReference.of();
        scheduler.scheduleTask(this, ticks, reference);
      }
    };
  }

  public boolean invokeEvent(final RandomEvent event) {
    final Game game = this.map.getGame();
    final ApiEventBus eventBus = EventBusProvider.getBus();
    return eventBus.post(RandomGameEvent.class, event, game);
  }
}
