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

import static java.util.Objects.requireNonNull;

import java.util.concurrent.atomic.AtomicBoolean;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public final class Moon extends SurvivorGadget implements Listener {

  private final Game game;
  private final AtomicBoolean activated;

  public Moon(final Game game) {
    super(
      "moon",
      GameProperties.MOON_COST,
      ItemFactory.createGadget("moon", GameProperties.MOON_MATERIAL, Message.MOON_NAME.build(), Message.MOON_LORE.build())
    );
    this.game = game;
    this.activated = new AtomicBoolean(false);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    if (activated.get()) {
      return true;
    }
    this.activated.set(true);

    final Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    final int duration = GameProperties.MOON_DURATION;
    final double gravity = GameProperties.MOON_GRAVITY;

    final GameScheduler scheduler = game.getScheduler();
    manager.applyToAllParticipants(participant -> {
      final AttributeInstance attribute = requireNonNull(participant.getAttribute(Attribute.GRAVITY));
      attribute.setBaseValue(gravity);

      final StrictPlayerReference reference = StrictPlayerReference.of(participant);
      scheduler.scheduleTask(() -> participant.resetAttribute(Attribute.GRAVITY), duration, reference);

      final PlayerAudience audience = participant.getAudience();
      audience.sendMessage(Message.MOON_ACTIVATE.build());
    });

    final NullReference ref = NullReference.of();
    scheduler.scheduleTask(() -> this.activated.set(false), duration, ref);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.MOON_SOUND);

    return false;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerDamage(final EntityDamageEvent event) {
    final Entity entity = event.getEntity();
    if (!(entity instanceof final Player player)) {
      return;
    }

    final GamePlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    if (!this.activated.get()) {
      return;
    }

    final EntityDamageEvent.DamageCause cause = event.getCause();
    if (cause != EntityDamageEvent.DamageCause.FALL) {
      return;
    }

    event.setCancelled(true);
  }
}
