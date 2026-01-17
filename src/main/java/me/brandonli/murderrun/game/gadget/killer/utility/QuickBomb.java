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
package me.brandonli.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.TNTPrimed;

public final class QuickBomb extends KillerGadget {

  public QuickBomb(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "quick_bomb",
        properties.getQuickBombCost(),
        ItemFactory.createGadget(
            "quick_bomb",
            properties.getQuickBombMaterial(),
            Message.QUICK_BOMB_NAME.build(),
            Message.QUICK_BOMB_LORE.build()));
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    manager.applyToLivingSurvivors(this::spawnPrimedTnt);

    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getQuickBombSound());

    return false;
  }

  private void spawnPrimedTnt(final GamePlayer survivor) {
    final Game game = survivor.getGame();
    final GameProperties properties = game.getProperties();
    final Location location = survivor.getLocation();
    final World world = requireNonNull(location.getWorld());
    final int bombTicks = properties.getQuickBombTicks();
    final double bombDamage = properties.getQuickBombDamage();
    world.spawn(location, TNTPrimed.class, tnt -> {
      tnt.setFuseTicks(bombTicks);
      tnt.setYield((float) bombDamage);
    });
  }
}
