/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.gadget.killer.utility;

import java.util.Set;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
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
      GameProperties.BLOOD_CURSE_COST,
      ItemFactory.createGadget(
        "blood_curse",
        GameProperties.BLOOD_CURSE_MATERIAL,
        Message.BLOOD_CURSE_NAME.build(),
        Message.BLOOD_CURSE_LORE.build()
      )
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
    scheduler.scheduleRepeatedTask(() -> manager.applyToLivingSurvivors(this::setBloodBlock), 0, 7L, reference);
    manager.playSoundForAllParticipants(GameProperties.BLOOD_CURSE_SOUND);

    final Component msg = Message.BLOOD_CURSE_ACTIVATE.build();
    manager.sendMessageToAllLivingSurvivors(msg);

    final GamePlayer player = packet.getPlayer();
    final PlayerAudience audience = player.getAudience();
    audience.sendMessage(Message.BLOOD_CURSE_ACTIVATE_KILLER.build());

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
