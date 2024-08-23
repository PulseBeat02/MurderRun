package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerStartupTool;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class ResurrectionStone extends SurvivorGadget {

  public ResurrectionStone() {
    super(
        "resurrection_stone",
        Material.BEACON,
        Message.RESURRECTION_STONE_NAME.build(),
        Message.RESURRECTION_STONE_LORE.build(),
        128);
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
    scheduler.scheduleRepeatedTask(() -> this.spawnParticles(location), 0L, 1, 5 * 20L);
    scheduler.scheduleTask(() -> this.resurrectPlayer(game, closest), 5 * 20L);
  }

  private void spawnParticles(final Location location) {
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DRAGON_BREATH, location, 5, 0.5, 0.5, 0.5);
    location.add(0, 0.05, 0);
  }

  private void resurrectPlayer(final Game game, final GamePlayer closest) {

    final PlayerManager playerManager = game.getPlayerManager();
    final PlayerStartupTool temp = new PlayerStartupTool(playerManager);
    temp.handleInnocent(closest);
    closest.setAlive(true);
    playerManager.resetCachedPlayers();

    closest.apply(resurrected -> {
      final Location death = requireNonNull(resurrected.getLastDeathLocation());
      resurrected.setHealth(20);
      resurrected.setFoodLevel(20);
      resurrected.setSaturation(20);
      resurrected.teleport(death);
      resurrected.setGameMode(GameMode.SURVIVAL);
    });

    final Component message = Message.RESURRECTION_STONE_ACTIVATE.build();
    playerManager.applyToAllParticipants(gamePlayer -> gamePlayer.sendMessage(message));
  }
}
