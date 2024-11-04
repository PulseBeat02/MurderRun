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
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.trap.SurvivorTrap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public final class TrapVest extends SurvivorGadget {

  public TrapVest() {
    super("trap_vest", Material.IRON_DOOR, Message.TRAP_VEST_NAME.build(), Message.TRAP_VEST_LORE.build(), GameProperties.TRAP_VEST_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GadgetManager gadgetManager = game.getGadgetManager();
    final DeathManager deathManager = player.getDeathManager();
    final PlayerDeathTask task = new PlayerDeathTask(() -> this.handleTraps(gadgetManager, player, world), false);
    deathManager.addDeathTask(task);

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.TRAP_VEST_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GameProperties.TRAP_VEST_SOUND);

    return false;
  }

  private void handleTraps(final GadgetManager manager, final GamePlayer player, final World world) {
    final PlayerInventory inventory = player.getInventory();
    final Location location = requireNonNull(player.getDeathLocation());
    final ItemStack[] slots = inventory.getContents();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();

    for (final ItemStack slot : slots) {
      if (slot == null) {
        return;
      }

      final Item droppedItem = world.dropItem(location, slot);
      final ItemStack stack = droppedItem.getItemStack();
      final Gadget gadget = mechanism.getGadgetFromStack(stack);
      if (gadget instanceof SurvivorTrap) {
        final Vector velocity = new Vector(
          (RandomUtils.generateDouble() - 0.5) * 2,
          RandomUtils.generateDouble() * 2,
          (RandomUtils.generateDouble() - 0.5) * 2
        );
        droppedItem.setVelocity(velocity);
      }
    }
  }
}
