package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerStartupTool;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ResurrectionStone extends SurvivorGadget {

  public ResurrectionStone() {
    super(
        "resurrection_stone",
        Material.BEACON,
        Locale.RESURRECTION_STONE_TRAP_NAME.build(),
        Locale.RESURRECTION_STONE_TRAP_LORE.build(),
        128);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final GadgetManager gadgetManager = game.getGadgetManager();
    final int range = gadgetManager.getActivationRange();

    final GamePlayer closest = requireNonNull(this.getClosestDeadPlayer(game, location));
    final Location closestLocation = closest.getLocation();
    final double distance = location.distanceSquared(closestLocation);
    if (distance > range * range) {
      return;
    }

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnParticles(location), 0L, 2L, 5 * 20L);
    scheduler.scheduleTask(() -> this.resurrectPlayer(game, closest), 5 * 20L);
  }

  private void spawnParticles(final Location location) {
    final World world = requireNonNull(location.getWorld());
    world.spawnParticle(Particle.DRAGON_BREATH, location, 10, 0.5, 0.5, 0.5);
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

    final Component message = Locale.RESURRECTION_STONE_TRAP_ACTIVATE.build();
    playerManager.applyToAllParticipants(gamePlayer -> gamePlayer.sendMessage(message));
  }

  private @Nullable GamePlayer getClosestDeadPlayer(final Game game, final Location origin) {
    final PlayerManager playerManager = game.getPlayerManager();
    final Collection<GamePlayer> players = playerManager.getDead();
    final double min = Double.MAX_VALUE;
    GamePlayer closest = null;
    for (final GamePlayer player : players) {
      final Location location = player.getDeathLocation();
      if (location == null) {
        continue;
      }
      final double distance = location.distanceSquared(origin);
      if (distance < min) {
        closest = player;
      }
    }
    return closest;
  }
}
