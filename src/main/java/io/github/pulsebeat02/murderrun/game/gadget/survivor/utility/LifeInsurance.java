package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import java.util.Collection;
import java.util.Iterator;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitTask;

public final class LifeInsurance extends Gadget {

  private final Multimap<GamePlayer, BukkitTask> taskMap;

  public LifeInsurance() {
    super(
        "life_insurance",
        Material.RED_DYE,
        Locale.LIFE_INSURANCE_TRAP_NAME.build(),
        Locale.LIFE_INSURANCE_TRAP_LORE.build());
    this.taskMap = ArrayListMultimap.create();
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final GameSettings settings = game.getSettings();
    final Arena arena = settings.getArena();
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final World world = first.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    final Component message = Locale.LIFE_INSURANCE_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final GameScheduler scheduler = game.getScheduler();
    final BukkitTask task = scheduler.scheduleTask(
        () -> manager.applyToAllMurderers(
            killer -> this.checkKillerDistance(killer, gamePlayer, world, first, second)),
        20L);
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

    if (distance <= 16) {

      final double[] coords = MapUtils.generateFriendlyRandomXZ(first, second);
      final Location temp = new Location(world, coords[0], 0, coords[1]);
      final Block block = world.getHighestBlockAt(temp);
      final Location top = block.getLocation();
      player.teleport(top);

      final Collection<BukkitTask> tasks = this.taskMap.get(player);
      final Iterator<BukkitTask> iterator = tasks.iterator();
      final BukkitTask task = iterator.next();
      iterator.remove();
      task.cancel();
    }
  }
}
