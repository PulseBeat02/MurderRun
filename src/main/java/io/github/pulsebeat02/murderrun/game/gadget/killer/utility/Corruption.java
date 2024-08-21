package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerResetTool;
import io.github.pulsebeat02.murderrun.game.player.PlayerStartupTool;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Corruption extends KillerGadget {

  public Corruption() {
    super(
        "corruption",
        Material.ZOMBIE_HEAD,
        Message.CORRUPTION_NAME.build(),
        Message.CORRUPTION_LORE.build(),
        96);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final GadgetManager gadgetManager = game.getGadgetManager();
    final int range = gadgetManager.getActivationRange();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer closest = manager.getNearestDeadSurvivor(location);
    if (closest == null) {
      return;
    }

    final Location closestLocation = closest.getDeathLocation();
    if (closestLocation == null) {
      return;
    }

    final double distance = location.distanceSquared(closestLocation);
    if (distance > range * range) {
      return;
    }

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnParticles(location), 0L, 2L, 5 * 20L);
    scheduler.scheduleTask(() -> this.corruptPlayer(game, closest), 5 * 20L);
  }

  private void corruptPlayer(final Game game, final GamePlayer closest) {

    final PlayerManager manager = game.getPlayerManager();
    manager.promoteToKiller(closest);

    final PlayerResetTool tool = new PlayerResetTool(manager);
    tool.handlePlayer(closest);

    final PlayerStartupTool temp = new PlayerStartupTool(manager);
    temp.handleMurderer(closest);
    closest.apply(Corruption::resetStats);

    final Component message = Message.CORRUPTION_ACTIVATE.build();
    manager.applyToAllParticipants(gamePlayer -> gamePlayer.sendMessage(message));
  }

  private static void resetStats(final Player resurrected) {

    final Location death = requireNonNull(resurrected.getLastDeathLocation());
    resurrected.teleport(death);

    final ItemStack stack = ItemUtils.createKillerSword();
    final PlayerInventory inventory = resurrected.getInventory();
    inventory.addItem(stack);
  }

  private void spawnParticles(final Location location) {
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DUST, location, 10, 0.5, 0.5, 0.5, new DustOptions(Color.RED, 4));
    location.add(0, 0.05, 0);
  }
}
