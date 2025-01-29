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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.PlayerReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class HackTrap extends SurvivorTrap {

  public HackTrap() {
    super(
      "hack",
      Material.EMERALD_BLOCK,
      Message.HACK_NAME.build(),
      Message.HACK_LORE.build(),
      Message.HACK_ACTIVATE.build(),
      GameProperties.HACK_COST,
      Color.GREEN
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final PlayerInventory inventory = murderer.getInventory();
    final ItemStack stack = this.getSword(inventory);
    if (stack == null) {
      return;
    }

    final PlayerReference reference = PlayerReference.of(murderer);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> inventory.addItem(stack), GameProperties.HACK_DURATION, reference);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.HACK_SOUND);
  }

  private @Nullable ItemStack getSword(final PlayerInventory inventory) {
    final ItemStack[] slots = inventory.getContents();
    ItemStack find = null;
    for (final ItemStack stack : slots) {
      if (PDCUtils.isSword(stack)) {
        find = stack;
        inventory.remove(find);
        break;
      }
    }
    return find;
  }
}
