package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadgetManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerStartConfigurator;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ResurrectionStone extends MurderGadget {

  public ResurrectionStone() {
    super(
        "resurrection_stone",
        Material.BEACON,
        Locale.RESURRECTION_STONE_TRAP_NAME.build(),
        Locale.RESURRECTION_STONE_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final MurderGadgetManager gadgetManager = game.getGadgetManager();
    final int range = gadgetManager.getActivationRange();

    final GamePlayer closest = this.getClosestDeadPlayer(game, location);
    if (closest == null) {
      return;
    }

    final Location closestLocation = closest.getLocation();
    final double distance = location.distanceSquared(closestLocation);
    if (distance > range * range) {
      return;
    }

    final MurderPlayerManager playerManager = game.getPlayerManager();
    final PlayerStartConfigurator temp = new PlayerStartConfigurator(playerManager);
    temp.handleInnocent(closest);
    closest.setAlive(true);
    playerManager.resetCachedPlayers();

    closest.apply(resurrected -> {
      final Location death = resurrected.getLastDeathLocation();
      if (death == null) {
        throw new AssertionError("Death location is null!");
      }
      resurrected.setHealth(20);
      resurrected.setFoodLevel(20);
      resurrected.setSaturation(20);
      resurrected.teleport(death);
      resurrected.setGameMode(GameMode.SURVIVAL);
    });

    final Component message = Locale.RESURRECTION_STONE_TRAP_ACTIVATE.build();
    playerManager.applyToAllParticipants(gamePlayer -> gamePlayer.sendMessage(message));
  }

  private @Nullable GamePlayer getClosestDeadPlayer(final MurderGame game, final Location origin) {
    final MurderPlayerManager playerManager = game.getPlayerManager();
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
