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

import java.util.Collection;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.Killer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;

public final class Forewarn extends KillerGadget {

  public Forewarn(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "forewarn",
      properties.getForewarnCost(),
      ItemFactory.createGadget("forewarn", properties.getForewarnMaterial(), Message.FOREWARN_NAME.build(), Message.FOREWARN_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final GamePlayerManager manager = game.getPlayerManager();
    if (!(player instanceof final Killer killer)) {
      return true;
    }
    item.remove();

    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(killer);
    scheduler.scheduleRepeatedTask(() -> this.handleInnocents(manager, killer), 0, 20L, reference);

    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = player.getAudience();
    final Component msg = Message.FOREWARN_ACTIVATE.build();
    audience.sendMessage(msg);
    audience.playSound(properties.getForewarnSound());

    return false;
  }

  private void handleInnocents(final GamePlayerManager manager, final Killer gamePlayer) {
    manager.applyToLivingSurvivors(survivor -> this.handleForewarn(survivor, gamePlayer));
  }

  private void handleForewarn(final GamePlayer gamePlayer, final Killer player) {
    final Collection<GamePlayer> set = player.getForewarnGlowing();
    if (!(gamePlayer instanceof final Survivor survivor)) {
      return;
    }

    final MetadataManager metadata = player.getMetadataManager();
    if (survivor.hasCarPart()) {
      set.add(survivor);
      metadata.setEntityGlowing(survivor, ChatColor.RED, true);
    } else if (set.contains(survivor)) {
      set.remove(player);
      metadata.setEntityGlowing(survivor, ChatColor.RED, false);
    }
  }
}
