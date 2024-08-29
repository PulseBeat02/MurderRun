package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public final class AllSeeingEye extends KillerGadget {

  private static final String ALL_SEEING_EYE_SOUND = "entity.ender_eye.death";

  public AllSeeingEye() {
    super(
        "all_seeing_eye",
        Material.ENDER_EYE,
        Message.ALL_SEEING_EYE_NAME.build(),
        Message.ALL_SEEING_EYE_LORE.build(),
        32);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final Survivor random = manager.getRandomAliveInnocentPlayer();
    final Location before = player.getLocation();
    this.setPlayerState(player, random);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.resetPlayerState(player, before), 7 * 20L);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(ALL_SEEING_EYE_SOUND);

    return false;
  }

  private void resetPlayerState(final GamePlayer player, final Location location) {
    player.teleport(location);
    player.setGameMode(GameMode.SURVIVAL);
    player.setSpectatorTarget(null);
    player.setAllowSpectatorTeleport(true);
  }

  private void setPlayerState(final GamePlayer player, final GamePlayer survivor) {
    final Player internal = survivor.getInternalPlayer();
    player.setGameMode(GameMode.SPECTATOR);
    player.setAllowSpectatorTeleport(false);
    player.setSpectatorTarget(internal);
  }
}
