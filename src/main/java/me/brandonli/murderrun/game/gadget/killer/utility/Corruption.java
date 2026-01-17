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
import me.brandonli.murderrun.game.gadget.GadgetManager;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.death.DeathManager;
import me.brandonli.murderrun.game.player.phase.PlayerResetTool;
import me.brandonli.murderrun.game.player.phase.PlayerStartupTool;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.LoosePlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class Corruption extends KillerGadget {

  public Corruption(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "corruption",
        properties.getCorruptionCost(),
        ItemFactory.createGadget(
            "corruption",
            properties.getCorruptionMaterial(),
            Message.CORRUPTION_NAME.build(),
            Message.CORRUPTION_LORE.build()));
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final Location location = player.getLocation();
    final GadgetManager gadgetManager = game.getGadgetManager();
    final double range = gadgetManager.getActivationRange();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer closest = manager.getNearestDeadSurvivor(location);
    if (closest == null) {
      return true;
    }

    final Location closestLocation = closest.getDeathLocation();
    if (closestLocation == null) {
      return true;
    }

    final double distance = location.distanceSquared(closestLocation);
    if (distance > range * range) {
      return true;
    }
    item.remove();

    final GameScheduler scheduler = game.getScheduler();
    final LoosePlayerReference reference = LoosePlayerReference.of(closest);
    scheduler.scheduleRepeatedTask(() -> this.spawnParticles(location), 0, 5, 5 * 20L, reference);
    scheduler.scheduleTask(() -> this.corruptPlayer(game, closest), 5 * 20L, reference);

    return false;
  }

  private void corruptPlayer(final Game game, final GamePlayer closest) {
    final GamePlayerManager manager = game.getPlayerManager();
    manager.promoteToKiller(closest);

    final PlayerResetTool tool = new PlayerResetTool(manager);
    tool.handlePlayer(closest);

    final PlayerStartupTool temp = new PlayerStartupTool(manager);
    temp.handleMurderer(closest);

    final Location death = requireNonNull(closest.getDeathLocation());
    closest.teleport(death);

    final GameProperties properties = game.getProperties();
    final ItemStack stack = ItemFactory.createKillerSword(properties);
    final ItemStack[] gear = ItemFactory.createKillerGear(properties);
    final PlayerInventory inventory = closest.getInventory();
    inventory.addItem(stack);
    inventory.setArmorContents(gear);

    final PersistentDataContainer container = closest.getPersistentDataContainer();
    container.set(Keys.KILLER_ROLE, PersistentDataType.BOOLEAN, true);

    final DeathManager deathManager = closest.getDeathManager();
    final NPC stand = deathManager.getCorpse();
    if (stand != null) {
      stand.destroy();
    }

    final Component message = Message.CORRUPTION_ACTIVATE.build();
    manager.sendMessageToAllParticipants(message);
  }

  private void spawnParticles(final Location location) {
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 10, 0.5, 0.5, 0.5, new DustOptions(Color.RED, 4));
    location.add(0, 0.05, 0);
  }
}
