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
package me.brandonli.murderrun.game.gadget.killer.utility;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.checkerframework.checker.nullness.qual.KeyFor;

public final class IcePath extends KillerGadget {

  public IcePath() {
    super(
      "ice_path",
      GameProperties.ICE_PATH_COST,
      ItemFactory.createGadget("ice_path", GameProperties.ICE_PATH_MATERIAL, Message.ICE_PATH_NAME.build(), Message.ICE_PATH_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.setIceTrail(game, player), 0, 4, 20 * 60L, reference);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.ICE_PATH_SOUND);

    return false;
  }

  private void setIceTrail(final Game game, final GamePlayer player) {
    final Location location = player.getLocation();
    final Map<Location, Material> originalBlocks = new HashMap<>();
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        final Location clone = location.clone();
        final Location blockLocation = clone.add(x, -1, z);
        final Block block = blockLocation.getBlock();
        final Material type = block.getType();
        if (!type.equals(Material.ICE)) {
          originalBlocks.put(blockLocation, type);
          block.setType(Material.ICE);
        }
      }
    }

    final Map<Location, Material> blocksToRestore = new HashMap<>(originalBlocks);
    final GameScheduler scheduler = game.getScheduler();
    final NullReference reference = NullReference.of();
    scheduler.scheduleTask(
      () -> {
        final Collection<Entry<@KeyFor("blocksToRestore") Location, Material>> entries = blocksToRestore.entrySet();
        for (final Map.Entry<Location, Material> entry : entries) {
          final Location blockLocation = entry.getKey();
          final Block block = blockLocation.getBlock();
          final Material material = entry.getValue();
          block.setType(material);
          block.getState().update(true);
        }
      },
      2 * 20L,
      reference
    );
  }
}
