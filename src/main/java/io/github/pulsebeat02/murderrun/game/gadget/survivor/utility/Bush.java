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

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.StrictPlayerReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
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

  public Bush() {
    super(
      "bush",
      GameProperties.BUSH_COST,
      ItemFactory.createGadget("bush", GameProperties.BUSH_MATERIAL, Message.BUSH_NAME.build(), Message.BUSH_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final int duration = GameProperties.BUSH_DURATION;
    player.addPotionEffects(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1));

    final PlayerInventory inventory = player.getInventory();
    final Location location = player.getLocation();
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    scheduler.scheduleRepeatedTask(() -> this.createFacingLocation(player, location), 0, 5, duration, reference);

    final ItemStack[] before = inventory.getArmorContents();
    final ItemStack[] empty = new ItemStack[4];
    inventory.setArmorContents(empty);
    scheduler.scheduleTask(() -> inventory.setArmorContents(before), duration, reference);

    final NullReference emptyRef = NullReference.of();
    final Block block = location.getBlock();
    block.setType(Material.OAK_LEAVES);
    scheduler.scheduleTask(() -> block.setType(Material.AIR), duration, emptyRef);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.BUSH_SOUND);

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
