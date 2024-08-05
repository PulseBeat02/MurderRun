package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import io.github.pulsebeat02.murderrun.game.arena.MurderArena;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.utils.MapUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class LifeInsurance extends MurderGadget {

  public LifeInsurance() {
    super(
        "life_insurance",
        Material.RED_DYE,
        Locale.LIFE_INSURANCE_TRAP_NAME.build(),
        Locale.LIFE_INSURANCE_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onDropEvent(game, event, true);

    final Player player = event.getPlayer();
    final MurderPlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final MurderSettings settings = game.getSettings();
    final MurderArena arena = settings.getArena();
    final Location first = arena.getFirstCorner();
    final Location second = arena.getSecondCorner();
    final World world = first.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }

    final Component message = Locale.LIFE_INSURANCE_ACTIVATE.build();
    gamePlayer.sendMessage(message);

    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(
        () -> manager.applyToAllMurderers(
            killer -> this.checkKillerDistance(killer, gamePlayer, world, first, second)),
        20L);
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
    }
  }
}
