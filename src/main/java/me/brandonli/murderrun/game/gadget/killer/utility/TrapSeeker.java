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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.Gadget;
import me.brandonli.murderrun.game.gadget.GadgetLoadingMechanism;
import me.brandonli.murderrun.game.gadget.GadgetManager;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.Killer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public final class TrapSeeker extends KillerGadget {

  public TrapSeeker(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "trap_seeker",
      properties.getTrapSeekerCost(),
      ItemFactory.createGadget(
        "trap_seeker",
        properties.getTrapSeekerMaterial(),
        Message.TRAP_SEEKER_NAME.build(),
        Message.TRAP_SEEKER_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    if (!(player instanceof final Killer killer)) {
      return true;
    }
    item.remove();

    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(killer);
    scheduler.scheduleRepeatedTask(() -> this.handleTrapSeeking(game, killer), 0, 20L, reference);

    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = killer.getAudience();
    final Component message = Message.TRAP_SEEKER_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(properties.getTrapSeekerSound());

    return false;
  }

  private void handleTrapSeeking(final Game game, final Killer killer) {
    final GadgetManager manager = game.getGadgetManager();
    final Location origin = killer.getLocation();
    final World world = requireNonNull(origin.getWorld());
    final GameProperties properties = game.getProperties();
    final double radius = properties.getTrapSeekerRadius();
    final Collection<Entity> entities = world.getNearbyEntities(origin, radius, radius, radius);
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    final Set<Item> gadgets = new HashSet<>();

    for (final Entity entity : entities) {
      if (!(entity instanceof final Item item)) {
        continue;
      }

      final ItemStack stack = item.getItemStack();
      final Gadget gadget = mechanism.getGadgetFromStack(stack);
      if (gadget == null) {
        continue;
      }

      if (gadget instanceof KillerGadget) {
        return;
      }

      gadgets.add(item);
    }

    final Collection<Item> set = killer.getGlowingTraps();
    final MetadataManager metadata = killer.getMetadataManager();
    for (final Item item : gadgets) {
      if (!set.contains(item)) {
        set.add(item);
        metadata.setEntityGlowing(item, ChatColor.YELLOW, true);
      }
    }

    for (final Item entity : set) {
      if (!gadgets.contains(entity)) {
        set.remove(entity);
        metadata.setEntityGlowing(entity, ChatColor.YELLOW, false);
      }
    }
  }
}
