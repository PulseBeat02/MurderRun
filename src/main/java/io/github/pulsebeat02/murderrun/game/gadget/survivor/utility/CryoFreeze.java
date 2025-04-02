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
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.map.BlockWhitelistManager;
import io.github.pulsebeat02.murderrun.game.map.GameMap;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

public final class CryoFreeze extends SurvivorGadget {

  public CryoFreeze() {
    super(
      "cryo_freeze",
      GameProperties.CRYO_FREEZE_COST,
      ItemFactory.createGadget("cryo_freeze", Material.ICE, Message.CRYO_FREEZE_NAME.build(), Message.CRYO_FREEZE_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GameMap map = game.getMap();
    final BlockWhitelistManager whitelistManager = map.getBlockWhitelistManager();
    final int cx = location.getBlockX();
    final int cy = location.getBlockY();
    final int cz = location.getBlockZ();

    final int radius = GameProperties.CRYO_FREEZE_RADIUS;
    for (int x = -radius; x <= radius; x++) {
      for (int y = 0; y <= radius; y++) {
        for (int z = -radius; z <= radius; z++) {
          final double distance = Math.sqrt((double) x * x + (double) y * y + (double) z * z);
          if (distance >= radius - 0.5 && distance <= radius + 0.5) {
            final Block block = world.getBlockAt(cx + x, cy + y, cz + z);
            block.setType(Material.PACKED_ICE);
            whitelistManager.addWhitelistedBlock(block);
          }
        }
      }
    }

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.CRYO_FREEZE_SOUND);

    return false;
  }
}
