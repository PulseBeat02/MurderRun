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
package io.github.pulsebeat02.murderrun.gui.arena;

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
import org.bukkit.event.EventPriority;
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
    final BiConsumer<Player, Location> add
  ) {
    this.plugin = plugin;
    this.locations = locations;
    this.remove = remove;
    this.add = add;
  }

  public void registerEvents() {
    final Server server = this.plugin.getServer();
    final PluginManager manager = server.getPluginManager();
    manager.registerEvents(this, this.plugin);
  }

  public void runScheduledTask() {
    final BukkitScheduler scheduler = Bukkit.getScheduler();
    scheduler.runTaskTimer(this.plugin, this::checkPlayerHand, 0, 10);
  }

  private void checkPlayerHand() {
    final Collection<? extends Player> online = Bukkit.getOnlinePlayers();
    for (final Player player : online) {
      final PlayerInventory inventory = player.getInventory();
      final ItemStack item = inventory.getItemInMainHand();
      this.sendGlowingPackets(player, item);
    }
  }

  private void sendGlowingPackets(final Player player, final ItemStack item) {
    if (PDCUtils.isWand(item)) {
      this.locations.forEach(loc -> PacketToolsProvider.PACKET_API.setBlockGlowing(player, loc, true));
    } else {
      this.locations.forEach(loc -> PacketToolsProvider.PACKET_API.setBlockGlowing(player, loc, false));
    }
  }

  public void unregister() {
    final HandlerList handlerList = PlayerInteractEvent.getHandlerList();
    handlerList.unregister(this);
  }

  @EventHandler(priority = EventPriority.LOWEST)
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
