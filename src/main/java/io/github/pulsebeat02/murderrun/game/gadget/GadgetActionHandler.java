package io.github.pulsebeat02.murderrun.game.gadget;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameStatus;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerApparatus;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorApparatus;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
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

  public GadgetActionHandler(final GadgetManager manager) {
    this.manager = manager;
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

    final Player player = event.getPlayer();
    if (this.checkKillerStatus(player)) {
      event.setCancelled(true);
      return;
    }

    final Game game = this.manager.getGame();
    final ItemStack stack = event.getItem();
    final Action action = event.getAction();
    if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    this.handleEventLogic(stack, gadget -> gadget.onGadgetRightClick(game, event, false));
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
    item.setUnlimitedLifetime(true);
    item.setPickupDelay(Integer.MAX_VALUE);

    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);

    this.handleEventLogic(stack, gadget -> {
      final boolean cancel = gadget.onGadgetDrop(game, gamePlayer, item, false);
      event.setCancelled(cancel);
    });
  }

  private boolean checkKillerStatus(final Player player) {

    final Game game = this.manager.getGame();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final MurderRun plugin = this.manager.getPlugin();
    final AudienceProvider provider = plugin.getAudience();
    final BukkitAudiences audiences = provider.retrieve();
    final UUID uuid = player.getUniqueId();
    final Audience audience = audiences.player(uuid);
    final GameStatus status = game.getStatus();
    final boolean invalidKiller =
        gamePlayer instanceof Killer && status != GameStatus.KILLERS_RELEASED;
    final boolean invalidSurvivor = gamePlayer instanceof Survivor
        && (status == GameStatus.NOT_STARTED || status == GameStatus.FINISHED);
    if (invalidKiller || invalidSurvivor) {
      audience.sendMessage(Message.MISUSE_GADGET_ERROR.build());
      return true;
    }

    return false;
  }

  private void runGadgetDetectionTask() {
    final Game game = this.manager.getGame();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(this::onNearGadget, 0, 5);
  }

  private void registerEvents() {
    final MurderRun plugin = this.manager.getPlugin();
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  private void onNearGadget() {
    final Game game = this.manager.getGame();
    final PlayerManager playerManager = game.getPlayerManager();
    playerManager.applyToAllParticipants(this::handlePlayerGadgetLogic);
  }

  private void handlePlayerGadgetLogic(final GamePlayer player) {

    final GadgetSearchResult result = this.getGetClosestTrap(player);
    if (result == null) {
      return;
    }

    final Gadget gadget = result.gadget;
    final Item item = result.item;
    if (player instanceof final Killer killer && killer.isIgnoringTraps()) {
      item.remove();
      return;
    }

    final Game game = this.manager.getGame();
    gadget.onGadgetNearby(game, player, item);
  }

  private @Nullable GadgetSearchResult getGetClosestTrap(final GamePlayer player) {

    final Location origin = player.getLocation();
    final World world = requireNonNull(origin.getWorld());
    final double range = this.manager.getActivationRange();
    final Collection<Entity> entities = world.getNearbyEntities(origin, range, range, range);
    final GadgetLoadingMechanism mechanism = this.manager.getMechanism();
    final boolean isSurvivor = player instanceof Survivor;

    double min = Double.MAX_VALUE;
    Gadget closest = null;
    Item closestItem = null;
    for (final Entity entity : entities) {

      if (!(entity instanceof final Item item)) {
        continue;
      }

      final ItemStack stack = item.getItemStack();
      final Gadget gadget = mechanism.getGadgetFromStack(stack);
      if (gadget == null) {
        continue;
      }

      final boolean activate = isSurvivor && (gadget instanceof KillerApparatus)
          || !isSurvivor && (gadget instanceof SurvivorApparatus);
      if (activate) {
        final Location location = item.getLocation();
        final double distance = origin.distanceSquared(location);
        if (distance < min) {
          min = distance;
          closest = gadget;
          closestItem = item;
        }
      }
    }

    if (closestItem == null || closest == null) { // checker framework
      return null;
    }

    if (closest instanceof Trap) {
      closestItem.remove(); // checker framework
    }

    return new GadgetSearchResult(closest, closestItem);
  }

  private void handleEventLogic(final @Nullable ItemStack stack, final Consumer<Gadget> gadget) {

    if (stack == null) {
      return;
    }

    if (!PDCUtils.isGadget(stack)) {
      return;
    }

    final String data =
        PDCUtils.getPersistentDataAttribute(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING);
    if (data == null) {
      return;
    }

    final GadgetLoadingMechanism mechanism = this.manager.getMechanism();
    final Map<String, Gadget> gadgets = mechanism.getGameGadgets();
    final Gadget tool = requireNonNull(gadgets.get(data));
    gadget.accept(tool);
  }

  record GadgetSearchResult(Gadget gadget, Item item) {}
}
