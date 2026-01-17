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
package me.brandonli.murderrun.game.gadget.packet;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class GadgetRightClickPacket {

  private final Game game;
  private final GamePlayer player;
  private final ItemStack itemStack;

  public GadgetRightClickPacket(
      final Game game, final GamePlayer player, final ItemStack itemStack) {
    this.game = game;
    this.player = player;
    this.itemStack = itemStack;
  }

  public static GadgetRightClickPacket create(final Game game, final PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    final ItemStack item = requireNonNull(event.getItem());
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    return new GadgetRightClickPacket(game, gamePlayer, item);
  }

  public Game getGame() {
    return this.game;
  }

  public GamePlayer getPlayer() {
    return this.player;
  }

  public ItemStack getItemStack() {
    return this.itemStack;
  }
}
