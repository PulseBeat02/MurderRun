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
package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.phase.PlayerResetTool;
import io.github.pulsebeat02.murderrun.game.player.phase.PlayerStartupTool;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.LoosePlayerReference;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class Corruption extends KillerGadget {

  public Corruption() {
    super(
      "corruption",
      Material.ZOMBIE_HEAD,
      Message.CORRUPTION_NAME.build(),
      Message.CORRUPTION_LORE.build(),
      GameProperties.CORRUPTION_COST
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

    final ItemStack stack = ItemFactory.createKillerSword();
    final ItemStack[] gear = ItemFactory.createKillerGear();
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
