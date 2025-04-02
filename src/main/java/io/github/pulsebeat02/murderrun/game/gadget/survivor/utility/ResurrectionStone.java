/*

MIT License

Copyright (c) 2024 Brandon Li

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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.phase.PlayerStartupTool;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.LoosePlayerReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
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
