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
package me.brandonli.murderrun.gui.arena;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.UUID;
import java.util.function.BiConsumer;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.utils.GlowUtils;
import me.brandonli.murderrun.utils.PDCUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

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

  private Team registerTeam(@UnderInitialization WandListener this) {
    final UUID uuid = UUID.randomUUID();
    final String name = uuid.toString();
    final Server server = Bukkit.getServer();
    final ScoreboardManager manager = requireNonNull(server.getScoreboardManager());
    final Scoreboard scoreboard = manager.getMainScoreboard();
    final Team team = scoreboard.registerNewTeam(name);
    team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    team.setCanSeeFriendlyInvisibles(true);
    return team;
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
      this.locations.forEach(loc -> {
          final Slime slime = GlowUtils.setBlockGlowing(player, loc, true);
          if (slime == null) {
            return;
          }
          slime.setSize(2);
        });
    } else {
      this.locations.forEach(loc -> GlowUtils.setBlockGlowing(player, loc, false));
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
      GlowUtils.setBlockGlowing(player, location, false);
      this.remove.accept(player, location);
    } else {
      this.add.accept(player, location);
    }
  }
}
