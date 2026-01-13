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
package me.brandonli.murderrun.api.event;

import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.api.event.contract.GameStatusEvent;
import me.brandonli.murderrun.api.event.contract.ability.AbilityUseEvent;
import me.brandonli.murderrun.api.event.contract.arena.ArenaEvent;
import me.brandonli.murderrun.api.event.contract.arena.ArenaModificationType;
import me.brandonli.murderrun.api.event.contract.gadget.GadgetUseEvent;
import me.brandonli.murderrun.api.event.contract.gadget.TrapActivateEvent;
import me.brandonli.murderrun.api.event.contract.lobby.LobbyEvent;
import me.brandonli.murderrun.api.event.contract.lobby.LobbyModificationType;
import me.brandonli.murderrun.api.event.contract.statistic.StatisticsEvent;
import me.brandonli.murderrun.api.event.contract.statistic.StatisticsType;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.ability.AbilityRegistry;
import me.brandonli.murderrun.game.gadget.GadgetRegistry;
import me.brandonli.murderrun.game.player.GamePlayer;

public final class EventBusTests {

  private final MurderRun plugin;

  public EventBusTests(final MurderRun plugin) {
    this.plugin = plugin;
  }

  @SuppressWarnings("nullness")
  public void runTestUnits() {
    final ApiEventBus eventBus = EventBusProvider.getBus();
    eventBus.subscribe(this.plugin, MurderRunEvent.class, event -> {
      System.out.println("Hello from %s".format(event.getEventType().toGenericString()));
    });
    eventBus.post(AbilityUseEvent.class, AbilityRegistry.getRegistry().getAbility("teleport"), new GamePlayer(null, null));
    eventBus.post(ArenaEvent.class, this.plugin.getArenaManager().getArenas().values().iterator().next(), ArenaModificationType.CREATION);
    eventBus.post(ArenaEvent.class, this.plugin.getArenaManager().getArenas().values().iterator().next(), ArenaModificationType.DELETION);
    eventBus.post(GadgetUseEvent.class, GadgetRegistry.getRegistry().getGadget("crash"), new GamePlayer(null, null));
    eventBus.post(TrapActivateEvent.class, GadgetRegistry.getRegistry().getGadget("crash"), new GamePlayer(null, null));
    eventBus.post(LobbyEvent.class, this.plugin.getLobbyManager().getLobbies().values().iterator().next(), LobbyModificationType.CREATION);
    eventBus.post(LobbyEvent.class, this.plugin.getLobbyManager().getLobbies().values().iterator().next(), LobbyModificationType.DELETION);
    eventBus.post(StatisticsEvent.class, StatisticsType.FASTEST_KILLER_WIN, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.FASTEST_SURVIVOR_WIN, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.TOTAL_DEATHS, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.TOTAL_GAMES, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.TOTAL_KILLS, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.TOTAL_LOSSES, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.TOTAL_WINS, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.WIN_LOSS_RATIO, 1);
    eventBus.post(GameStatusEvent.class, new GameStatus(null), new Game(null));
  }
}
