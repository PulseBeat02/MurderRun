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

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.api.event.contract.GameStatusEvent;
import me.brandonli.murderrun.api.event.contract.ability.AbilityUseEvent;
import me.brandonli.murderrun.api.event.contract.arena.ArenaEvent;
import me.brandonli.murderrun.api.event.contract.arena.ArenaModificationType;
import me.brandonli.murderrun.api.event.contract.event.RandomGameEvent;
import me.brandonli.murderrun.api.event.contract.gadget.GadgetUseEvent;
import me.brandonli.murderrun.api.event.contract.gadget.TrapActivateEvent;
import me.brandonli.murderrun.api.event.contract.lobby.LobbyEvent;
import me.brandonli.murderrun.api.event.contract.lobby.LobbyModificationType;
import me.brandonli.murderrun.api.event.contract.statistic.StatisticsEvent;
import me.brandonli.murderrun.api.event.contract.statistic.StatisticsType;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameStatus;
import me.brandonli.murderrun.game.ability.Ability;
import me.brandonli.murderrun.game.ability.AbilityRegistry;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.arena.ArenaManager;
import me.brandonli.murderrun.game.gadget.Gadget;
import me.brandonli.murderrun.game.gadget.GadgetRegistry;
import me.brandonli.murderrun.game.lobby.Lobby;
import me.brandonli.murderrun.game.lobby.LobbyManager;
import me.brandonli.murderrun.game.map.ambience.SmiteEvent;
import me.brandonli.murderrun.game.player.GamePlayer;
import org.slf4j.Logger;

public final class EventBusTests {

  private final MurderRun plugin;

  public EventBusTests(final MurderRun plugin) {
    this.plugin = plugin;
  }

  public void runTestUnits() {
    final ApiEventBus eventBus = EventBusProvider.getBus();
    final Logger logger = this.plugin.getSLF4JLogger();
    eventBus.subscribe(this.plugin, MurderRunEvent.class, event -> {
      final Class<?> type = event.getEventType();
      final String name = type.toGenericString();
      logger.info("Hello from {}", name);
    });

    final Game game = new Game(this.plugin);
    final GameStatus status = game.getStatus();
    final UUID uuid = UUID.randomUUID();
    final GamePlayer player = new GamePlayer(game, uuid);
    final AbilityRegistry abilityRegistry = AbilityRegistry.getRegistry();
    final Collection<Ability> abilities = abilityRegistry.getAbilities();
    final Ability ability = abilities.iterator().next();
    final GadgetRegistry gadgetRegistry = GadgetRegistry.getRegistry();
    final Collection<Gadget> gadgets = gadgetRegistry.getGadgets();
    final Gadget gadget = gadgets.iterator().next();
    final ArenaManager arenaManager = this.plugin.getArenaManager();
    final Map<String, Arena> arenas = arenaManager.getArenas();
    final LobbyManager lobbyManager = this.plugin.getLobbyManager();
    final Map<String, Lobby> lobbies = lobbyManager.getLobbies();
    eventBus.post(AbilityUseEvent.class, ability, player);
    eventBus.post(GadgetUseEvent.class, gadget, player);
    eventBus.post(TrapActivateEvent.class, gadget, player);
    for (final Arena arena : arenas.values()) {
      eventBus.post(ArenaEvent.class, arena, ArenaModificationType.CREATION);
      eventBus.post(ArenaEvent.class, arena, ArenaModificationType.DELETION);
      break;
    }
    for (final Lobby lobby : lobbies.values()) {
      eventBus.post(LobbyEvent.class, lobby, LobbyModificationType.CREATION);
      eventBus.post(LobbyEvent.class, lobby, LobbyModificationType.DELETION);
      break;
    }
    eventBus.post(StatisticsEvent.class, StatisticsType.FASTEST_KILLER_WIN, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.FASTEST_SURVIVOR_WIN, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.TOTAL_DEATHS, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.TOTAL_GAMES, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.TOTAL_KILLS, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.TOTAL_LOSSES, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.TOTAL_WINS, 1);
    eventBus.post(StatisticsEvent.class, StatisticsType.WIN_LOSS_RATIO, 1);
    eventBus.post(GameStatusEvent.class, status, game);
    eventBus.post(RandomGameEvent.class, new SmiteEvent(), game);
  }
}
