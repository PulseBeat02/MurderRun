package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
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

public final class Phantom extends KillerGadget {

  private static final String PHANTOM_SOUND = "entity.phantom.ambient";
  private static final int PHANTOM_DURATION = 15 * 20;

  public Phantom() {
    super(
        "phantom",
        Material.PHANTOM_MEMBRANE,
        Message.PHANTOM_NAME.build(),
        Message.PHANTOM_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    player.setGameMode(GameMode.SPECTATOR);

    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    gamePlayer.setAllowSpectatorTeleport(false);

    final Location old = player.getLocation();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.setDefault(gamePlayer, old), PHANTOM_DURATION);

    final PlayerAudience audience = gamePlayer.getAudience();
    audience.playSound(PHANTOM_SOUND);
  }

  private void setDefault(final GamePlayer player, final Location location) {
    player.setAllowSpectatorTeleport(true);
    player.teleport(location);
    player.setGameMode(GameMode.SURVIVAL);
  }
}
