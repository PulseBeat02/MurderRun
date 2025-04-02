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
package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.api.event.ApiEventBus;
import io.github.pulsebeat02.murderrun.api.event.EventBusProvider;
import io.github.pulsebeat02.murderrun.api.event.contract.gadget.GadgetUseEvent;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameStatus;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerDevice;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetNearbyPacket;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetRightClickPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorDevice;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.NullReference;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.util.Collection;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GadgetActionHandler implements Listener {

  private final GadgetManager manager;
  private final ApiEventBus bus;

  public GadgetActionHandler(final GadgetManager manager) {
    this.manager = manager;
    this.bus = EventBusProvider.getBus();
  }

  public void start() {
    this.registerEvents();
    this.runGadgetDetectionTask();
  }

  public void shutdown() {
    HandlerList.unregisterAll(this);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onRightClick(final PlayerInteractEvent event) {
    final Action action = event.getAction();
    if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    final Player player = event.getPlayer();
    if (this.checkKillerStatus(player)) {
      event.setCancelled(true);
      return;
    }

    final Game game = this.manager.getGame();
    final ItemStack stack = event.getItem();
    if (stack == null) {
      return;
    }

    final String data = PDCUtils.getPersistentDataAttribute(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING);
    if (data == null) {
      return;
    }

    final GadgetRightClickPacket packet = GadgetRightClickPacket.create(game, event);
    final GadgetLoadingMechanism mechanism = this.manager.getMechanism();
    final Map<String, Gadget> gadgets = mechanism.getGameGadgets();
    final Gadget tool = requireNonNull(gadgets.get(data));
    if (this.bus.post(GadgetUseEvent.class, tool)) {
      return;
    }

    final boolean result = tool.onGadgetRightClick(packet);
    if (result) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onDropItem(final PlayerDropItemEvent event) {
    final Player player = event.getPlayer();
    if (this.checkKillerStatus(player)) {
      event.setCancelled(true);
      return;
    }

    final Game game = this.manager.getGame();
    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    final String data = PDCUtils.getPersistentDataAttribute(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING);
    if (data == null) {
      return;
    }

    final GadgetDropPacket packet = GadgetDropPacket.create(game, event);
    final GadgetLoadingMechanism mechanism = this.manager.getMechanism();
    final Map<String, Gadget> gadgets = mechanism.getGameGadgets();
    final Gadget tool = requireNonNull(gadgets.get(data));
    if (this.bus.post(GadgetUseEvent.class, tool)) {
      return;
    }

    final boolean result = tool.onGadgetDrop(packet);
    if (result) {
      event.setCancelled(true);
    }

    item.setPickupDelay(Integer.MAX_VALUE);
    item.setUnlimitedLifetime(true);
  }

  private boolean checkKillerStatus(final Player player) {
    final Game game = this.manager.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return false;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final GameStatus status = game.getStatus();
    final GameStatus.Status gameStatus = status.getStatus();
    final boolean invalidKiller = gamePlayer instanceof Killer && gameStatus != GameStatus.Status.KILLERS_RELEASED;
    final boolean invalidSurvivor =
      gamePlayer instanceof Survivor && (gameStatus == GameStatus.Status.NOT_STARTED || gameStatus == GameStatus.Status.FINISHED);
    return invalidKiller || invalidSurvivor;
  }

  private void runGadgetDetectionTask() {
    final Game game = this.manager.getGame();
    final GameScheduler scheduler = game.getScheduler();
    final NullReference reference = NullReference.of();
    scheduler.scheduleRepeatedTask(this::onNearGadget, 0, 5, reference);
  }

  private void registerEvents() {
    final MurderRun plugin = this.manager.getPlugin();
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  private void onNearGadget() {
    final Game game = this.manager.getGame();
    final GamePlayerManager playerManager = game.getPlayerManager();
    playerManager.applyToAllParticipants(this::handlePlayerGadgetLogic);
  }

  private void handlePlayerGadgetLogic(final GamePlayer player) {
    final GadgetSearchResult result = this.getGetClosestTrap(player);
    final Gadget gadget = result.gadget;
    final Item item = result.item;
    if (gadget == null || item == null) {
      return;
    }

    final boolean ignore = player instanceof final Killer killer && killer.isIgnoringTraps();
    if (ignore) {
      item.remove();
      return;
    }

    final Game game = this.manager.getGame();
    final GadgetNearbyPacket packet = new GadgetNearbyPacket(game, player, item);
    gadget.onGadgetNearby(packet);
  }

  private GadgetSearchResult getGetClosestTrap(final GamePlayer player) {
    final Location origin = player.getLocation();
    final World world = requireNonNull(origin.getWorld());
    final double range = this.manager.getActivationRange();
    final Collection<Entity> entities = world.getNearbyEntities(origin, range, range, range, this::checkEntityPredicate);
    final boolean isSurvivor = player instanceof Survivor;
    double min = Double.MAX_VALUE;
    Gadget closest = null;
    Item closestItem = null;
    for (final Entity entity : entities) {
      if (!(entity instanceof final Item item)) {
        continue;
      }

      final ItemStack stack = item.getItemStack();
      final GadgetLoadingMechanism mechanism = this.manager.getMechanism();
      final Gadget gadget = mechanism.getGadgetFromStack(stack);
      final boolean activate = isSurvivor ? gadget instanceof KillerDevice : gadget instanceof SurvivorDevice;
      if (!activate) {
        continue;
      }

      final Location location = item.getLocation();
      final double distance = origin.distanceSquared(location);
      if (distance < min) {
        min = distance;
        closest = gadget;
        closestItem = item;
      }
    }
    return new GadgetSearchResult(closest, closestItem);
  }

  private boolean checkEntityPredicate(final Entity entity) {
    if (!(entity instanceof final Item item)) {
      return false;
    }

    final ItemStack stack = item.getItemStack();
    return PDCUtils.isGadget(stack);
  }

  record GadgetSearchResult(@Nullable Gadget gadget, @Nullable Item item) {}
}
