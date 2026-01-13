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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class Bush extends SurvivorGadget {

  public Bush(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "bush",
      properties.getBushCost(),
      ItemFactory.createGadget("bush", properties.getBushMaterial(), Message.BUSH_NAME.build(), Message.BUSH_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GameProperties properties = game.getProperties();
    final int duration = properties.getBushDuration();
    player.addPotionEffects(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1));

    final PlayerInventory inventory = player.getInventory();
    final Location location = player.getLocation();
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    scheduler.scheduleRepeatedTask(() -> this.createFacingLocation(player, location), 0, 5, duration, reference);

    @SuppressWarnings("all") // checker
    final ItemStack[] before = inventory.getArmorContents();
    final ItemStack[] empty = new ItemStack[4];
    inventory.setArmorContents(empty);
    scheduler.scheduleTask(() -> inventory.setArmorContents(before), duration, reference);

    final NullReference emptyRef = NullReference.of();
    final Block block = location.getBlock();
    block.setType(Material.OAK_LEAVES);
    scheduler.scheduleTask(() -> block.setType(Material.AIR), duration, emptyRef);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getBushSound());

    return false;
  }

  private void createFacingLocation(final GamePlayer target, final Location origin) {
    final Location newLocation = target.getLocation();
    final Vector direction = newLocation.getDirection();
    final Location clone = origin.clone();
    clone.setDirection(direction);
    target.teleport(clone);
  }
}
