/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.map.MapUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;

public final class LifeInsurance extends SurvivorGadget {

  public LifeInsurance() {
    super(
      "life_insurance",
      Material.RED_DYE,
      Message.LIFE_INSURANCE_NAME.build(),
      Message.LIFE_INSURANCE_LORE.build(),
      GameProperties.LIFE_INSURANCE_COST
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
    final BukkitTask task = scheduler.scheduleRepeatedTask(internalTask, 0, 20L, reference);
    final Collection<BukkitTask> tasks = survivor.getLifeInsuranceTasks();
    tasks.add(task);

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.LIFE_INSURANCE_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GameProperties.LIFE_INSURANCE_SOUND);

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
    final double radius = GameProperties.LIFE_INSURANCE_RADIUS;
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
