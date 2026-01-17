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

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Map;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;

public final class AbilityActionHandler implements Listener {

  private final AbilityManager manager;
  private final Map<String, Ability> abilities;

  public AbilityActionHandler(final AbilityManager manager) {
    final AbilityLoadingMechanism mechanism = manager.getMechanism();
    final MurderRun plugin = manager.getPlugin();
    this.manager = manager;
    this.abilities = mechanism.getGameAbilities();
  }

  public void start() {
    final Server server = Bukkit.getServer();
    final PluginManager pluginManager = server.getPluginManager();
    final MurderRun plugin = this.manager.getPlugin();
    pluginManager.registerEvents(this, plugin);
    final Collection<Ability> abilityList = this.abilities.values();
    for (final Ability ability : abilityList) {
      ability.start();
    }
  }

  public void shutdown() {
    HandlerList.unregisterAll(this);
    final Collection<Ability> abilityList = this.abilities.values();
    for (final Ability ability : abilityList) {
      ability.shutdown();
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerFish(final PlayerFishEvent event) {
    final PlayerFishEvent.State state = event.getState();
    if (state != PlayerFishEvent.State.FISHING) {
      return;
    }

    final Player player = event.getPlayer();
    final Game game = this.manager.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final PlayerInventory inventory = player.getInventory();
    final EquipmentSlot hand = requireNonNull(event.getHand());
    final ItemStack rod = requireNonNull(inventory.getItem(hand));
    if (!PDCUtils.isAbility(rod)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final int cooldown = gamePlayer.getCooldown(rod);
    if (cooldown > 0) {
      event.setCancelled(true);
      return;
    }

    gamePlayer.setCooldown(rod, 0);
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerThrow(final PlayerDropItemEvent event) {
    final Player player = event.getPlayer();
    final Game game = this.manager.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    final String data = PDCUtils.getPersistentDataAttribute(
        stack, Keys.ABILITY_KEY_NAME, PersistentDataType.STRING);
    if (data == null) {
      return;
    }
    event.setCancelled(true);
  }
}
