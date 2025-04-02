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

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.GadgetManager;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.death.DeathManager;
import me.brandonli.murderrun.game.player.phase.PlayerStartupTool;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.LoosePlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;

public final class ResurrectionStone extends SurvivorGadget {

  public ResurrectionStone() {
    super(
      "resurrection_stone",
      GameProperties.RESURRECTION_STONE_COST,
      ItemFactory.createGadget(
        "resurrection_stone",
        GameProperties.RESURRECTION_STONE_MATERIAL,
        Message.RESURRECTION_STONE_NAME.build(),
        Message.RESURRECTION_STONE_LORE.build()
      )
    );
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

    final DeathManager deathManager = closest.getDeathManager();
    final NPC corpse = requireNonNull(deathManager.getCorpse());
    final Location closestLocation = corpse.getStoredLocation();
    final double distance = location.distanceSquared(closestLocation);
    if (distance > range * range) {
      return true;
    }

    final LoosePlayerReference reference = LoosePlayerReference.of(closest);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnParticles(location), 0L, 1, 5 * 20L, reference);
    scheduler.scheduleTask(() -> this.resurrectPlayer(game, closest), 5 * 20L, reference);
    item.remove();

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.RESURRECTION_STONE_SOUND);

    return false;
  }

  private void spawnParticles(final Location location) {
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 5, 0.5, 0.5, 0.5, new DustOptions(Color.YELLOW, 4));
    location.add(0, 0.5, 0);
  }

  private void resurrectPlayer(final Game game, final GamePlayer closest) {
    final GamePlayerManager playerManager = game.getPlayerManager();
    final PlayerStartupTool temp = new PlayerStartupTool(playerManager);
    temp.handleInnocent(closest);
    closest.setAlive(true);

    final Location death = requireNonNull(closest.getDeathLocation());
    closest.clearInventory();
    closest.setGameMode(GameMode.SURVIVAL);
    closest.setHealth(20);
    closest.setFoodLevel(20);
    closest.setSaturation(20);
    closest.teleport(death);

    final DeathManager manager = closest.getDeathManager();
    final NPC corpse = requireNonNull(manager.getCorpse());
    corpse.destroy();

    final Component message = Message.RESURRECTION_STONE_ACTIVATE.build();
    playerManager.sendMessageToAllParticipants(message);
  }
}
