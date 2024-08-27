package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerStartupTool;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.PlayerInventory;

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

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final GadgetManager gadgetManager = game.getGadgetManager();
    final double range = gadgetManager.getActivationRange();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer closest = manager.getNearestDeadSurvivor(location);
    if (closest == null) {
      super.onGadgetDrop(game, event, false);
      return;
    }

    final DeathManager deathManager = closest.getDeathManager();
    final ArmorStand corpse = requireNonNull(deathManager.getCorpse());
    final Location closestLocation = corpse.getLocation();
    final double distance = location.distanceSquared(closestLocation);
    if (distance > range * range) {
      super.onGadgetDrop(game, event, false);
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final PlayerAudience audience = gamePlayer.getAudience();
    audience.playSound("block.end_portal_frame.fill");

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnParticles(location), 0L, 1, 5 * 20L);
    scheduler.scheduleTask(() -> this.resurrectPlayer(game, closest), 5 * 20L);

    super.onGadgetDrop(game, event, true);
  }

  private void spawnParticles(final Location location) {
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(
        Particle.DUST, location, 5, 0.5, 0.5, 0.5, new DustOptions(Color.YELLOW, 4));
    location.add(0, 0.5, 0);
  }

  private void resurrectPlayer(final Game game, final GamePlayer closest) {

    final PlayerManager playerManager = game.getPlayerManager();
    final PlayerStartupTool temp = new PlayerStartupTool(playerManager);
    temp.handleInnocent(closest);
    closest.setAlive(true);
    playerManager.resetCachedPlayers();

    closest.apply(resurrected -> {
      final Location death = requireNonNull(resurrected.getLastDeathLocation());
      final PlayerInventory inventory = resurrected.getInventory();
      inventory.clear();
      resurrected.setHealth(20);
      resurrected.setFoodLevel(20);
      resurrected.setSaturation(20);
      resurrected.teleport(death);
      resurrected.setGameMode(GameMode.SURVIVAL);
    });

    final DeathManager manager = closest.getDeathManager();
    final ArmorStand corpse = requireNonNull(manager.getCorpse());
    corpse.remove();

    final Component message = Message.RESURRECTION_STONE_ACTIVATE.build();
    playerManager.sendMessageToAllParticipants(message);
  }
}
