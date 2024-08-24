package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
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
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitTask;

public final class LifeInsurance extends SurvivorGadget {

  private final Multimap<GamePlayer, BukkitTask> taskMap;

  public LifeInsurance() {
    super(
        "life_insurance",
        Material.RED_DYE,
        Message.LIFE_INSURANCE_NAME.build(),
        Message.LIFE_INSURANCE_LORE.build(),
        32);
    this.taskMap = ArrayListMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final World world = requireNonNull(first.getWorld());

    final Component message = Message.LIFE_INSURANCE_ACTIVATE.build();
    gamePlayer.sendMessage(message);
    gamePlayer.playSound("item.totem.use");

    final GameScheduler scheduler = game.getScheduler();
    final Consumer<Killer> consumer =
        killer -> this.checkKillerDistance(killer, gamePlayer, world, first, second);
    final BukkitTask task =
        scheduler.scheduleRepeatedTask(() -> manager.applyToAllMurderers(consumer), 0, 20L);
    this.taskMap.put(gamePlayer, task);
  }

  private void checkKillerDistance(
      final GamePlayer killer,
      final GamePlayer player,
      final World world,
      final Location first,
      final Location second) {

    final Location origin = player.getLocation();
    final Location killerLocation = killer.getLocation();
    final double distance = killerLocation.distanceSquared(origin);

    if (distance < 16) {

      final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
      final Location temp = new Location(world, coords[0], 0, coords[1]);
      final Block block = world.getHighestBlockAt(temp);
      final Location top = block.getLocation();
      final Location teleport = top.add(0, 1, 0);
      player.teleport(teleport);

      final Collection<BukkitTask> tasks = this.taskMap.get(player);
      final Iterator<BukkitTask> iterator = tasks.iterator();
      final BukkitTask task = iterator.next();
      iterator.remove();
      task.cancel();
    }
  }
}
