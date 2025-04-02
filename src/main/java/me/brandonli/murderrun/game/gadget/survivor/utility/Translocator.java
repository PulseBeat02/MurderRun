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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.packet.GadgetRightClickPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.InventoryUtils;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
import me.brandonli.murderrun.utils.map.MapUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public final class Translocator extends SurvivorGadget {

  public Translocator() {
    super(
      "translocator",
      GameProperties.TRANSLOCATOR_COST,
      ItemFactory.createTranslocator(
        ItemFactory.createGadget(
          "translocator",
          GameProperties.TRANSLOCATOR_MATERIAL,
          Message.TRANSLOCATOR_NAME.build(),
          Message.TRANSLOCATOR_LORE.build()
        )
      )
    );
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    final GamePlayer player = packet.getPlayer();
    final ItemStack stack = packet.getItemStack();
    if (stack == null) {
      return true;
    }

    final Material material = stack.getType();
    if (material != Material.LEVER) {
      return true;
    }

    final byte[] data = requireNonNull(PDCUtils.getPersistentDataAttribute(stack, Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY));
    final Location location = MapUtils.byteArrayToLocation(data);
    player.teleport(location);

    final PlayerInventory inventory = player.getInventory();
    InventoryUtils.consumeStack(inventory, stack);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.TRANSLOCATOR_SOUND);

    return false;
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final GamePlayer player = packet.getPlayer();
    final org.bukkit.entity.Item item = packet.getItem();

    final Location location = player.getLocation();
    final ItemStack stack = item.getItemStack();
    final byte[] bytes = MapUtils.locationToByteArray(location);
    Item.builder(stack)
      .lore(Message.TRANSLOCATOR_LORE1.build())
      .pdc(Keys.TRANSLOCATOR, PersistentDataType.BYTE_ARRAY, bytes)
      .model(null)
      .type(Material.LEVER);

    return true;
  }
}
