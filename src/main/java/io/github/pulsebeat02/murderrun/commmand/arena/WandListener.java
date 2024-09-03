package io.github.pulsebeat02.murderrun.commmand.arena;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.util.Collection;
import java.util.function.BiConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public final class WandListener implements Listener {

  private final MurderRun plugin;
  private final Collection<Location> locations;
  private final BiConsumer<Player, Location> remove;
  private final BiConsumer<Player, Location> add;

  public WandListener(
      final MurderRun plugin,
      final Collection<Location> locations,
      final BiConsumer<Player, Location> remove,
      final BiConsumer<Player, Location> add) {
    this.plugin = plugin;
    this.locations = locations;
    this.remove = remove;
    this.add = add;
    final Server server = plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    manager.registerEvents(this, plugin);
  }

  public void runScheduledTask() {
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.runTaskTimer(this.plugin, this::checkPlayerHand, 0, 10);
  }

  private void checkPlayerHand() {
    final Collection<? extends Player> online = Bukkit.getOnlinePlayers();
    online.forEach(player -> {
      final PlayerInventory inventory = player.getInventory();
      final ItemStack item = inventory.getItemInMainHand();
      if (PDCUtils.isWand(item)) {
        for (final Location loc : this.locations) {
          PacketToolsProvider.PACKET_API.setBlockGlowing(player, loc, true);
        }
      } else {
        for (final Location loc : this.locations) {
          PacketToolsProvider.PACKET_API.setBlockGlowing(player, loc, false);
        }
      }
    });
  }

  public void unregister() {
    final HandlerList handlerList = PlayerInteractEvent.getHandlerList();
    handlerList.unregister(this);
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
    if (this.locations.contains(location)) {
      PacketToolsProvider.PACKET_API.setBlockGlowing(player, location, false);
      this.remove.accept(player, location);
    } else {
      this.add.accept(player, location);
    }
  }
}
