package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Drone extends SurvivorGadget {

  public Drone() {
    super("drone", Material.ENDER_EYE, Message.DRONE_NAME.build(), Message.DRONE_LORE.build(), 32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final Location origin = player.getLocation();
    final Location drone = origin.add(0, 20, 0);
    player.setGameMode(GameMode.SPECTATOR);
    player.teleport(drone);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.setDefault(player, origin), 15 * 20L);
  }

  private void setDefault(final Player player, final Location origin) {
    player.teleport(origin);
    player.setGameMode(GameMode.ADVENTURE);
  }
}
