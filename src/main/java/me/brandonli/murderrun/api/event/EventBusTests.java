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

  @SuppressWarnings("all") // checker
  public void runTestUnits() {
    final ApiEventBus eventBus = EventBusProvider.getBus();
    eventBus.subscribe(plugin, MurderRunEvent.class, event -> {
      System.out.println("Hello from %s".format(event.getEventType().toGenericString()));
    });
    eventBus.post(AbilityUseEvent.class, AbilityRegistry.getRegistry().getAbility("teleport"), new GamePlayer(null, null));
    eventBus.post(ArenaEvent.class, plugin.getArenaManager().getArenas().values().iterator().next(), ArenaModificationType.CREATION);
    eventBus.post(ArenaEvent.class, plugin.getArenaManager().getArenas().values().iterator().next(), ArenaModificationType.DELETION);
    eventBus.post(GadgetUseEvent.class, GadgetRegistry.getRegistry().getGadget("crash"), new GamePlayer(null, null));
    eventBus.post(TrapActivateEvent.class, GadgetRegistry.getRegistry().getGadget("crash"), new GamePlayer(null, null));
    eventBus.post(LobbyEvent.class, plugin.getLobbyManager().getLobbies().values().iterator().next(), LobbyModificationType.CREATION);
    eventBus.post(LobbyEvent.class, plugin.getLobbyManager().getLobbies().values().iterator().next(), LobbyModificationType.DELETION);
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
