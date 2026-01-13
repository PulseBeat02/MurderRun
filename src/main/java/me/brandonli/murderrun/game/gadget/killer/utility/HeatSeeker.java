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
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class HeatSeeker extends KillerGadget {

  public HeatSeeker(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "heat_seeker",
      properties.getHeatSeekerCost(),
      ItemFactory.createGadget(
        "heat_seeker",
        properties.getHeatSeekerMaterial(),
        Message.HEAT_SEEKER_NAME.build(),
        Message.HEAT_SEEKER_LORE.build()
      )
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

    final StrictPlayerReference reference = StrictPlayerReference.of(killer);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.scheduleTasks(manager, killer), 0, 2 * 20L, reference);

    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = player.getAudience();
    final Component message = Message.HEAT_SEEKER_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(properties.getHeatSeekerSound());

    return false;
  }

  private void scheduleTasks(final GamePlayerManager manager, final Killer player) {
    manager.applyToLivingSurvivors(innocent -> this.handleGlowInnocent(innocent, player));
  }

  private void handleGlowInnocent(final GamePlayer innocent, final Killer owner) {
    final Location location = innocent.getLocation();
    final Location other = owner.getLocation();
    final Collection<GamePlayer> visible = owner.getHeatSeekerGlowing();
    final double distance = location.distanceSquared(other);
    final MetadataManager metadata = owner.getMetadataManager();
    final Game game = owner.getGame();
    final GameProperties properties = game.getProperties();
    final double radius = properties.getHeatSeekerRadius();
    if (distance < radius * radius) {
      visible.add(innocent);
      metadata.setEntityGlowing(innocent, NamedTextColor.RED, true);
    } else if (visible.contains(innocent)) {
      visible.remove(innocent);
      metadata.setEntityGlowing(innocent, NamedTextColor.RED, false);
    }
  }
}
