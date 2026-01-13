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

import static net.kyori.adventure.text.Component.empty;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Item;

public final class InfraredVision extends KillerGadget {

  public InfraredVision(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "infrared_vision",
      properties.getInfraredVisionCost(),
      ItemFactory.createGadget(
        "infrared_vision",
        properties.getInfraredVisionMaterial(),
        Message.INFRARED_VISION_NAME.build(),
        Message.INFRARED_VISION_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    final GameProperties properties = game.getProperties();
    manager.applyToLivingSurvivors(innocent -> this.setSurvivorGlow(scheduler, innocent, player));
    manager.playSoundForAllParticipants(properties.getInfraredVisionSound());

    return false;
  }

  private void setSurvivorGlow(final GameScheduler scheduler, final GamePlayer survivor, final GamePlayer killer) {
    final PlayerAudience audience = survivor.getAudience();
    final Component msg = Message.INFRARED_VISION_ACTIVATE.build();
    final Component title = Message.INFRARED_VISION_ACTIVATE_TITLE.build();
    final MetadataManager metadata = killer.getMetadataManager();
    final Game game = killer.getGame();
    final GameProperties properties = game.getProperties();
    metadata.setEntityGlowing(scheduler, survivor, NamedTextColor.RED, properties.getInfraredVisionDuration());
    audience.sendMessage(msg);
    audience.showTitle(empty(), title);
  }
}
