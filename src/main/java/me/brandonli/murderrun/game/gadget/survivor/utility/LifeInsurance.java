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

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import me.brandonli.murderrun.utils.map.MapUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;

public final class LifeInsurance extends SurvivorGadget {

  public LifeInsurance(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "life_insurance",
      properties.getLifeInsuranceCost(),
      ItemFactory.createGadget(
        "life_insurance",
        properties.getLifeInsuranceMaterial(),
        Message.LIFE_INSURANCE_NAME.build(),
        Message.LIFE_INSURANCE_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final GamePlayerManager manager = game.getPlayerManager();
    if (!(player instanceof final Survivor survivor)) {
      return true;
    }
    item.remove();

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final World world = requireNonNull(first.getWorld());

    final GameScheduler scheduler = game.getScheduler();
    final Consumer<GamePlayer> consumer = killer -> this.checkKillerDistance(killer, survivor, world, first, second);
    final Runnable internalTask = () -> manager.applyToKillers(consumer);
    final NullReference reference = NullReference.of();
    final BukkitTask task = scheduler.scheduleRepeatedTask(internalTask, 0, 10L, reference);
    final Collection<BukkitTask> tasks = survivor.getLifeInsuranceTasks();
    tasks.add(task);

    final PlayerAudience audience = player.getAudience();
    final GameProperties properties = game.getProperties();
    final Component message = Message.LIFE_INSURANCE_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(properties.getLifeInsuranceSound());

    return false;
  }

  private void checkKillerDistance(
    final GamePlayer killer,
    final Survivor player,
    final World world,
    final Location first,
    final Location second
  ) {
    final Location origin = player.getLocation();
    final Location killerLocation = killer.getLocation();
    final double distance = killerLocation.distanceSquared(origin);
    final Game game = player.getGame();
    final GameProperties properties = game.getProperties();
    final double radius = properties.getLifeInsuranceRadius();
    if (distance < radius * radius) {
      final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
      final Location temp = new Location(world, coords[0], 0, coords[1]);
      final Location teleport = MapUtils.getHighestSpawnLocation(temp);
      player.teleport(teleport);

      final Collection<BukkitTask> tasks = player.getLifeInsuranceTasks();
      final Iterator<BukkitTask> iterator = tasks.iterator();
      final BukkitTask task = iterator.next();
      iterator.remove();
      task.cancel();
    }
  }
}
