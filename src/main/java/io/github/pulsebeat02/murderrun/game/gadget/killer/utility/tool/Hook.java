package io.github.pulsebeat02.murderrun.game.gadget.killer.utility.tool;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.PDCUtils;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public final class Hook extends KillerGadget implements Listener {

  private final Game game;

  public Hook(final Game game) {
    super(
        "hook",
        Material.FISHING_ROD,
        Message.HOOK_NAME.build(),
        Message.HOOK_LORE.build(),
        GameProperties.HOOK_COST,
        ItemFactory::createHook);
    this.game = game;
  }

  @Override
  public void onGadgetRightClick(
      final Game game, final PlayerInteractEvent event, final boolean remove) {
    // allow right click
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerFish(final PlayerFishEvent event) {

    final State state = event.getState();
    if (state != State.CAUGHT_ENTITY) {
      return;
    }

    final Entity caught = event.getCaught();
    if (caught == null) {
      return;
    }

    if (!(caught instanceof final Player player)) {
      return;
    }

    final PlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final Player killer = event.getPlayer();
    final PlayerInventory inventory = killer.getInventory();
    final ItemStack hand = inventory.getItemInMainHand();
    if (!PDCUtils.isHook(hand)) {
      return;
    }

    final Vector multiplied = this.getMultipliedVelocity(killer, caught);
    caught.setVelocity(multiplied);
  }

  private Vector getMultipliedVelocity(final Player killer, final Entity caught) {
    final Location killerLocation = killer.getLocation();
    final Location caughtLocation = caught.getLocation();
    final Vector killerVector = killerLocation.toVector();
    final Vector caughtVector = caughtLocation.toVector();
    final Vector pullVector = killerVector.subtract(caughtVector);
    final Vector normalized = pullVector.normalize();
    return normalized.multiply(2);
  }
}
