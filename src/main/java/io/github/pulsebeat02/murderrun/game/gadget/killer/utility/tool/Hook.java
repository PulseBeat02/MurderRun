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
package io.github.pulsebeat02.murderrun.game.gadget.killer.utility.tool;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public final class Hook extends KillerGadget implements Listener {

  private final Game game;

  public Hook(final Game game) {
    super(
      "hook",
      Material.FISHING_ROD,
      Message.HOOK_NAME.build(),
      Message.HOOK_LORE.build(),
      GameProperties.HOOK_COST,
      ItemFactory::createHook
    );
    this.game = game;
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    return true;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerFish(final PlayerFishEvent event) {
    final State state = event.getState();
    if (state != State.CAUGHT_ENTITY) {
      return;
    }

    final Entity caught = event.getCaught();
    if (caught == null) {
      return;
    }

    if (!(caught instanceof final Player player)) {
      return;
    }

    final GamePlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final Player killer = event.getPlayer();
    final PlayerInventory inventory = killer.getInventory();
    final ItemStack hand = inventory.getItemInMainHand();
    if (!PDCUtils.isHook(hand)) {
      return;
    }

    final Vector multiplied = this.getMultipliedVelocity(killer, caught);
    caught.setVelocity(multiplied);
  }

  private Vector getMultipliedVelocity(final Player killer, final Entity caught) {
    final Location killerLocation = killer.getLocation();
    final Location caughtLocation = caught.getLocation();
    final Vector killerVector = killerLocation.toVector();
    final Vector caughtVector = caughtLocation.toVector();
    final Vector pullVector = killerVector.subtract(caughtVector);
    final Vector normalized = pullVector.normalize();
    return normalized.multiply(2);
  }
}
