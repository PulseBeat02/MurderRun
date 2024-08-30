package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;

public final class LifeInsurance extends SurvivorGadget {

  public LifeInsurance() {
    super(
        "life_insurance",
        Material.RED_DYE,
        Message.LIFE_INSURANCE_NAME.build(),
        Message.LIFE_INSURANCE_LORE.build(),
        32);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    if (!(player instanceof final Survivor survivor)) {
      return true;
    }

    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final World world = requireNonNull(first.getWorld());

    final GameScheduler scheduler = game.getScheduler();
    final Consumer<GamePlayer> consumer =
        killer -> this.checkKillerDistance(killer, survivor, world, first, second);
    final Runnable internalTask = () -> manager.applyToAllMurderers(consumer);
    final BukkitTask task = scheduler.scheduleRepeatedTask(internalTask, 0, 20L);
    final Collection<BukkitTask> tasks = survivor.getLifeInsuranceTasks();
    tasks.add(task);

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.LIFE_INSURANCE_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GadgetConstants.LIFE_INSURANCE_SOUND);

    return false;
  }

  private void checkKillerDistance(
      final GamePlayer killer,
      final Survivor player,
      final World world,
      final Location first,
      final Location second) {

    final Location origin = player.getLocation();
    final Location killerLocation = killer.getLocation();
    final double distance = killerLocation.distanceSquared(origin);
    final double radius = GadgetConstants.LIFE_INSURANCE_RADIUS;
    if (distance < radius * radius) {

      final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
      final Location temp = new Location(world, coords[0], 0, coords[1]);
      final Block block = world.getHighestBlockAt(temp);
      final Location top = block.getLocation();
      final Location teleport = top.add(0, 1, 0);
      player.teleport(teleport);

      final Collection<BukkitTask> tasks = player.getLifeInsuranceTasks();
      final Iterator<BukkitTask> iterator = tasks.iterator();
      final BukkitTask task = iterator.next();
      iterator.remove();
      task.cancel();
    }
  }
}
