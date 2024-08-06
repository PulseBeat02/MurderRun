package io.github.pulsebeat02.murderrun.game.gadget;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import io.github.pulsebeat02.murderrun.utils.Keys;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class GadgetActionHandler implements Listener {

  private final GadgetManager manager;

  public GadgetActionHandler(final GadgetManager manager) {
    this.manager = manager;
  }

  public void start() {
    final MurderRun plugin = this.manager.getPlugin();
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  public void shutdown() {
    HandlerList.unregisterAll(this);
  }

  @EventHandler
  public void onRightClick(final PlayerInteractEvent event) {
    final Game game = this.manager.getGame();
    final ItemStack stack = event.getItem();
    this.handleEventLogic(stack, gadget -> gadget.onGadgetRightClick(game, event, true));
  }

  @EventHandler
  public void onDropItem(final PlayerDropItemEvent event) {
    final Game game = this.manager.getGame();
    final Item item = event.getItemDrop();
    final ItemStack stack = item.getItemStack();
    this.handleEventLogic(stack, gadget -> gadget.onGadgetDrop(game, event, true));
  }

  public void onGadgetNear(final GamePlayer player) {

    final Location location = player.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      throw new AssertionError("Location doesn't have World attached to it!");
    }
  }

  private void handleEventLogic(final ItemStack stack, final Consumer<Gadget> gadget) {

    if (!ItemUtils.isGadget(stack)) {
      return;
    }

    if (stack == null) {
      return;
    }

    final String data = ItemUtils.getData(stack, Keys.GADGET_KEY_NAME, PersistentDataType.STRING);
    if (data == null) {
      return;
    }

    final GadgetLoadingMechanism mechanism = this.manager.getMechanism();
    final Map<String, Gadget> gadgets = mechanism.getGameGadgets();
    final Gadget tool = gadgets.get(data);
    gadget.accept(tool);
  }
}
