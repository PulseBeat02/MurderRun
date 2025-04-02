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
package me.brandonli.murderrun.game.ability;

import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.api.event.ApiEventBus;
import me.brandonli.murderrun.api.event.EventBusProvider;
import me.brandonli.murderrun.api.event.contract.ability.AbilityUseEvent;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.utils.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public abstract class AbstractAbility implements Ability {

  private final Item.Builder builder;
  private final String name;
  private final Game game;

  public AbstractAbility(final Game game, final String name, final Item.Builder builder) {
    this.game = game;
    this.name = name;
    this.builder = builder;
  }

  @Override
  public void start() {
    final Server server = Bukkit.getServer();
    final MurderRun plugin = this.game.getPlugin();
    final PluginManager pluginManager = server.getPluginManager();
    if (this instanceof final Listener listener) {
      pluginManager.registerEvents(listener, plugin);
    }
  }

  @Override
  public void shutdown() {}

  public boolean invokeEvent(final GamePlayer player) {
    final ApiEventBus eventBus = EventBusProvider.getBus();
    return eventBus.post(AbilityUseEvent.class, this, player);
  }

  @Override
  public Item.Builder getStackBuilder() {
    return this.builder;
  }

  @Override
  public String getId() {
    return this.name;
  }

  @Override
  public Game getGame() {
    return this.game;
  }
}
