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

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;

public final class BloodCurse extends KillerGadget {

  private static final Set<Material> BLACKLISTED_MATERIALS = Set.of(Material.AIR, Material.CHEST);

  public BloodCurse() {
    super(
      "blood_curse",
      Material.REDSTONE,
      Message.BLOOD_CURSE_NAME.build(),
      Message.BLOOD_CURSE_LORE.build(),
      GameProperties.BLOOD_CURSE_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final Item item = packet.getItem();
    item.remove();

    final GameScheduler scheduler = game.getScheduler();
    final GamePlayerManager manager = game.getPlayerManager();
    final NullReference reference = NullReference.of();
    scheduler.scheduleRepeatedTask(() -> manager.applyToSurvivors(this::setBloodBlock), 0, 7L, reference);
    manager.playSoundForAllParticipants(GameProperties.BLOOD_CURSE_SOUND);

    final Component msg = Message.BLOOD_CURSE_ACTIVATE.build();
    manager.sendMessageToAllLivingSurvivors(msg);

    return false;
  }

  private void setBloodBlock(final GamePlayer survivor) {
    final Location location = survivor.getLocation();
    final Block block = location.getBlock();
    final Block below = block.getRelative(BlockFace.DOWN);
    final Material type = below.getType();
    if (!type.isSolid() || !type.isOccluding() || BLACKLISTED_MATERIALS.contains(type)) {
      return;
    }

    block.setType(Material.REDSTONE_WIRE);
  }
}
