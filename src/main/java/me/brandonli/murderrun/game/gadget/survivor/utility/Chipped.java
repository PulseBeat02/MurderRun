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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;

public final class Chipped extends SurvivorGadget {

  public Chipped(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "chipped",
      properties.getChippedCost(),
      ItemFactory.createGadget("chipped", properties.getChippedMaterial(), Message.CHIPPED_NAME.build(), Message.CHIPPED_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    final MetadataManager metadata = player.getMetadataManager();
    final GameScheduler scheduler = game.getScheduler();
    this.setOtherSurvivorsGlowing(manager, metadata, scheduler);

    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getChippedSound());

    return false;
  }

  private void setOtherSurvivorsGlowing(final GamePlayerManager manager, final MetadataManager metadata, final GameScheduler scheduler) {
    final Game game = manager.getGame();
    final GameProperties properties = game.getProperties();
    manager.applyToLivingSurvivors(innocent ->
      metadata.setEntityGlowing(scheduler, innocent, ChatColor.GREEN, properties.getChippedDuration())
    );
  }
}
