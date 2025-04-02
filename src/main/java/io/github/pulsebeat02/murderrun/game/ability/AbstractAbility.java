/*

MIT License

Copyright (c) 2025 Brandon Li

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
package io.github.pulsebeat02.murderrun.game.ability;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;

public abstract class AbstractAbility implements Ability, Listener {

  private final Item.Builder builder;
  private final String name;
  private final Game game;

  public AbstractAbility(final Game game, final String name, final Item.Builder builder) {
    this.game = game;
    this.name = name;
    this.builder = builder;
  }

  @Override
  public void start() {
    final Server server = Bukkit.getServer();
    final MurderRun plugin = this.game.getPlugin();
    final PluginManager pluginManager = server.getPluginManager();
    pluginManager.registerEvents(this, plugin);
  }

  @EventHandler
  public void onPlayerFish(final PlayerFishEvent event) {
    final PlayerFishEvent.State state = event.getState();
    if (state != PlayerFishEvent.State.FISHING) {
      return;
    }

    final Player player = event.getPlayer();
    final GamePlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final PlayerInventory inventory = player.getInventory();
    final EquipmentSlot hand = requireNonNull(event.getHand());
    final ItemStack rod = requireNonNull(inventory.getItem(hand));
    if (!PDCUtils.isAbility(rod)) {
      return;
    }

    event.setCancelled(true);
  }

  @Override
  public void shutdown() {
    HandlerList.unregisterAll(this);
  }

  @Override
  public Item.Builder getStackBuilder() {
    return this.builder;
  }

  @Override
  public String getId() {
    return this.name;
  }
}
