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
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Deadringer extends SurvivorGadget {

  public Deadringer(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "deadringer",
      properties.getDeadringerCost(),
      ItemFactory.createGadget(
        "deadringer",
        properties.getDeadringerMaterial(),
        Message.DEADRINGER_NAME.build(),
        Message.DEADRINGER_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GameProperties properties = game.getProperties();
    final int duration = properties.getDeadringerDuration();
    player.setInvulnerable(true);
    player.addPotionEffects(
      new PotionEffect(PotionEffectType.SPEED, duration, 1, true, false),
      new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1, true, false)
    );

    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    scheduler.scheduleTask(() -> player.setInvulnerable(false), duration, reference);

    final GamePlayerManager manager = game.getPlayerManager();
    final String name = player.getDisplayName();
    final Component message = Message.PLAYER_DEATH.build(name);
    manager.sendMessageToAllParticipants(message);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getDeadringerSound());

    return false;
  }
}
