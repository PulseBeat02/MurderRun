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
package me.brandonli.murderrun.game.ability;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Map;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.immutable.Keys;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;

public final class AbilityActionHandler implements Listener {

  private final AbilityManager manager;
  private final Map<String, Ability> abilities;

  public AbilityActionHandler(final AbilityManager manager) {
    final AbilityLoadingMechanism mechanism = manager.getMechanism();
    final MurderRun plugin = manager.getPlugin();
    this.manager = manager;
    this.abilities = mechanism.getGameAbilities();
  }

  public void start() {
    final Server server = Bukkit.getServer();
    final PluginManager pluginManager = server.getPluginManager();
    final MurderRun plugin = this.manager.getPlugin();
    pluginManager.registerEvents(this, plugin);
    final Collection<Ability> abilityList = this.abilities.values();
    for (final Ability ability : abilityList) {
      ability.start();
    }
  }

  public void shutdown() {
    HandlerList.unregisterAll(this);
    final Collection<Ability> abilityList = this.abilities.values();
    for (final Ability ability : abilityList) {
      ability.shutdown();
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerFish(final PlayerFishEvent event) {
    final PlayerFishEvent.State state = event.getState();
    if (state != PlayerFishEvent.State.FISHING) {
      return;
    }

    final Player player = event.getPlayer();
    final Game game = this.manager.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final PlayerInventory inventory = player.getInventory();
    final EquipmentSlot hand = requireNonNull(event.getHand());
    final ItemStack rod = requireNonNull(inventory.getItem(hand));
    if (!PDCUtils.isAbility(rod)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final int cooldown = gamePlayer.getCooldown(rod);
    if (cooldown > 0) {
      event.setCancelled(true);
      return;
    }

    gamePlayer.setCooldown(rod, 0);
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerThrow(final PlayerDropItemEvent event) {
    final Player player = event.getPlayer();
    final Game game = this.manager.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    final String data = PDCUtils.getPersistentDataAttribute(stack, Keys.ABILITY_KEY_NAME, PersistentDataType.STRING);
    if (data == null) {
      return;
    }
    event.setCancelled(true);
  }
}
