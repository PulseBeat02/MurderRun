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
import me.brandonli.murderrun.game.player.Participant;
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

public final class FloorIsLava extends KillerGadget {

  public FloorIsLava(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "floor_is_lava",
      properties.getFloorIsLavaCost(),
      ItemFactory.createGadget(
        "floor_is_lava",
        properties.getFloorIsLavaMaterial(),
        Message.THE_FLOOR_IS_LAVA_NAME.build(),
        Message.THE_FLOOR_IS_LAVA_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final GamePlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    if (!(player instanceof final Killer killer)) {
      return true;
    }
    item.remove();

    final StrictPlayerReference reference = StrictPlayerReference.of(killer);
    scheduler.scheduleRepeatedTask(() -> this.handleSurvivors(manager, scheduler, killer), 0, 4 * 20L, reference);

    final GameProperties properties = game.getProperties();
    manager.applyToAllParticipants(this::sendFloorIsLavaMessage);
    manager.playSoundForAllParticipants(properties.getFloorIsLavaSound());

    return false;
  }

  private void handleSurvivors(final GamePlayerManager manager, final GameScheduler scheduler, final Killer killer) {
    manager.applyToLivingSurvivors(survivor -> this.handleMovement(scheduler, survivor, killer));
  }

  private void handleMovement(final GameScheduler scheduler, final GamePlayer player, final Killer killer) {
    final Location previous = player.getLocation();
    final StrictPlayerReference reference = StrictPlayerReference.of(killer);
    scheduler.scheduleTask(() -> this.handleLocationChecking(previous, player, killer), 3 * 20L, reference);
  }

  private void handleLocationChecking(final Location previous, final GamePlayer player, final Killer killer) {
    final Location newLocation = player.getLocation();
    final Collection<GamePlayer> glowing = killer.getFloorIsLavaGlowing();
    final MetadataManager metadata = killer.getMetadataManager();
    if (this.checkLocationSame(previous, newLocation)) {
      glowing.add(player);
      metadata.setEntityGlowing(player, NamedTextColor.RED, true);
    } else if (glowing.contains(player)) {
      glowing.remove(player);
      metadata.setEntityGlowing(player, NamedTextColor.RED, false);
    }
  }

  private boolean checkLocationSame(final Location first, final Location second) {
    return (first.getBlockX() == second.getBlockX() && first.getBlockY() == second.getBlockY() && first.getBlockZ() == second.getBlockZ());
  }

  private void sendFloorIsLavaMessage(final Participant participant) {
    final PlayerAudience audience = participant.getAudience();
    final Component msg = Message.THE_FLOOR_IS_LAVA_ACTIVATE.build();
    audience.sendMessage(msg);
  }
}
