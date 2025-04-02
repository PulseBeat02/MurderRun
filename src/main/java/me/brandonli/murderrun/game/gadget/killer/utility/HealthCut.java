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

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Item;

public final class HealthCut extends KillerGadget {

  public HealthCut() {
    super(
      "health_cut",
      GameProperties.HEALTH_CUT_COST,
      ItemFactory.createGadget(
        "health_cut",
        GameProperties.HEALTH_CUT_MATERIAL,
        Message.HEALTH_CUT_NAME.build(),
        Message.HEALTH_CUT_LORE.build()
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
    manager.applyToLivingSurvivors(survivor -> this.setState(survivor, scheduler));

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.HEALTH_CUT_SOUND);

    return false;
  }

  private void setState(final GamePlayer survivor, final GameScheduler scheduler) {
    final PlayerAudience audience = survivor.getAudience();
    final Component msg = Message.HEALTH_CUT_ACTIVATE.build();
    audience.sendMessage(msg);
    this.resetState(survivor, scheduler);
  }

  private void resetState(final GamePlayer survivor, final GameScheduler scheduler) {
    final StrictPlayerReference reference = StrictPlayerReference.of(survivor);
    final double before = survivor.getHealth();
    survivor.setHealth(2d);
    scheduler.scheduleTask(() -> survivor.setHealth(before), 5 * 20L, reference);
  }
}
