package io.github.pulsebeat02.murderrun.commmand.arena;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class WandListener implements Listener {

  private final MurderRun plugin;
  private final ArenaCommand command;

  public WandListener(final MurderRun plugin, final ArenaCommand command) {
    this.plugin = plugin;
    this.command = command;
    final Server server = plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    manager.registerEvents(this, plugin);
  }

  public void runScheduledTask() {
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.runTaskTimer(this.plugin, () -> this.checkPlayerHand(this.command), 0, 10);
  }

  private void checkPlayerHand(final ArenaCommand command) {
    final Collection<? extends Player> online = Bukkit.getOnlinePlayers();
    online.forEach(player -> {
      final PlayerInventory inventory = player.getInventory();
      final ItemStack item = inventory.getItemInMainHand();
      if (PDCUtils.isWand(item)) {
        final Collection<Location> locs = command.getItemLocations();
        for (final Location loc : locs) {
          PacketToolsProvider.PACKET_API.setBlockGlowing(player, loc, true);
        }
      } else {
        final Collection<Location> locs = command.getItemLocations();
        for (final Location loc : locs) {
          PacketToolsProvider.PACKET_API.setBlockGlowing(player, loc, false);
        }
      }
    });
  }

  @EventHandler
  public void onPlayerInteract(final PlayerInteractEvent event) {

    final Action action = event.getAction();
    if (action != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    final Block block = event.getClickedBlock();
    if (block == null) {
      return;
    }

    final ItemStack item = event.getItem();
    if (item == null) {
      return;
    }

    if (!PDCUtils.isWand(item)) {
      return;
    }

    final Player player = event.getPlayer();
    final Location location = block.getLocation();
    final Collection<Location> locs = this.command.getItemLocations();
    if (locs.contains(location)) {
      PacketToolsProvider.PACKET_API.setBlockGlowing(player, location, false);
      this.command.removeItemLocation(player, location);
    } else {
      this.command.addItemLocation(player, location);
    }
  }
}
