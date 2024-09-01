package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
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
import org.bukkit.entity.Item;

public final class ResurrectionStone extends SurvivorGadget {

  public ResurrectionStone() {
    super(
        "resurrection_stone",
        Material.BEACON,
        Message.RESURRECTION_STONE_NAME.build(),
        Message.RESURRECTION_STONE_LORE.build(),
        GameProperties.RESURECTION_STONE_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    final Location location = player.getLocation();
    final GadgetManager gadgetManager = game.getGadgetManager();
    final double range = gadgetManager.getActivationRange();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer closest = manager.getNearestDeadSurvivor(location);
    if (closest == null) {
      return super.onGadgetDrop(game, player, item, false);
    }

    final DeathManager deathManager = closest.getDeathManager();
    final ArmorStand corpse = requireNonNull(deathManager.getCorpse());
    final Location closestLocation = corpse.getLocation();
    final double distance = location.distanceSquared(closestLocation);
    if (distance > range * range) {
      return super.onGadgetDrop(game, player, item, false);
    }

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnParticles(location), 0L, 1, 5 * 20L);
    scheduler.scheduleTask(() -> this.resurrectPlayer(game, closest), 5 * 20L);
    super.onGadgetDrop(game, player, item, true);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.RESSURECTION_STONE_SOUND);

    return false;
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

    final Location death = requireNonNull(closest.getDeathLocation());
    closest.clearInventory();
    closest.setGameMode(GameMode.SURVIVAL);
    closest.setHealth(20);
    closest.setFoodLevel(20);
    closest.setSaturation(20);
    closest.teleport(death);

    final DeathManager manager = closest.getDeathManager();
    final ArmorStand corpse = requireNonNull(manager.getCorpse());
    corpse.remove();

    final Component message = Message.RESURRECTION_STONE_ACTIVATE.build();
    playerManager.sendMessageToAllParticipants(message);
  }
}
