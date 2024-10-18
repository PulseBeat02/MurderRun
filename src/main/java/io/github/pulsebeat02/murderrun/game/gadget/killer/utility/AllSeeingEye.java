package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
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

  public AllSeeingEye() {
    super(
      "all_seeing_eye",
      Material.ENDER_EYE,
      Message.ALL_SEEING_EYE_NAME.build(),
      Message.ALL_SEEING_EYE_LORE.build(),
      GameProperties.ALL_SEEING_EYE_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final PlayerManager manager = game.getPlayerManager();
    final Survivor random = manager.getRandomAliveInnocentPlayer();
    final Location before = player.getLocation();
    this.setPlayerState(player, random);

    final GameScheduler scheduler = game.getScheduler();
    final Player target = random.getInternalPlayer();
    final int duration = GameProperties.ALL_SEEING_EYE_DURATION;
    scheduler.scheduleRepeatedTask(() -> player.setSpectatorTarget(target), 0, 10, duration);
    scheduler.scheduleTask(() -> this.resetPlayerState(player, before), duration);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.ALL_SEEING_EYE_SOUND);

    return false;
  }

  private void resetPlayerState(final GamePlayer player, final Location location) {
    player.teleport(location);
    player.setSpectatorTarget(null);
    player.setAllowSpectatorTeleport(true);
    player.setGameMode(GameMode.SURVIVAL);
  }

  private void setPlayerState(final GamePlayer player, final GamePlayer survivor) {
    final Player internal = survivor.getInternalPlayer();
    player.setGameMode(GameMode.SPECTATOR);
    player.setAllowSpectatorTeleport(false);
    player.setSpectatorTarget(internal);
  }
}
