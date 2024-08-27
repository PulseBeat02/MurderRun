package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Drone extends SurvivorGadget {

  private static final int DRONE_DURATION = 15 * 20;
  private static final String DRONE_SOUND = "entity.phantom.flap";

  public Drone() {
    super(
        "drone",
        Material.PHANTOM_MEMBRANE,
        Message.DRONE_NAME.build(),
        Message.DRONE_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final Location origin = gamePlayer.getLocation();
    final Location clone = origin.clone();
    clone.add(0, 20, 0);

    gamePlayer.setGameMode(GameMode.SPECTATOR);
    gamePlayer.teleport(clone);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.resetPlayer(gamePlayer, origin), DRONE_DURATION);

    final PlayerAudience audience = gamePlayer.getAudience();
    audience.playSound(DRONE_SOUND);
  }

  private void resetPlayer(final GamePlayer player, final Location origin) {
    player.teleport(origin);
    player.setGameMode(GameMode.SURVIVAL);
  }
}
