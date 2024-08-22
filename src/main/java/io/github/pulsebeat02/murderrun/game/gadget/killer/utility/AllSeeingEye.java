package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public final class AllSeeingEye extends KillerGadget implements Listener {

  private final Set<GamePlayer> spectatorDisabled;
  private final Game game;

  public AllSeeingEye(final Game game) {
    super(
        "all_seeing_eye",
        Material.ENDER_EYE,
        Message.ALL_SEEING_EYE_NAME.build(),
        Message.ALL_SEEING_EYE_LORE.build(),
        32);
    this.spectatorDisabled = Collections.newSetFromMap(new WeakHashMap<>());
    this.game = game;
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final Survivor random = manager.getRandomAliveInnocentPlayer();
    final GamePlayer killer = manager.getGamePlayer(player);
    this.setPlayerState(killer, random);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.resetPlayerState(killer), 7 * 20L);
  }

  private void resetPlayerState(final GamePlayer player) {
    player.apply(raw -> {
      raw.setGameMode(GameMode.SURVIVAL);
      raw.setSpectatorTarget(null);
    });
    this.spectatorDisabled.remove(player);
  }

  private void setPlayerState(final GamePlayer player, final GamePlayer survivor) {
    player.apply(raw -> {
      raw.setGameMode(GameMode.SPECTATOR);
      survivor.apply(raw::setSpectatorTarget);
    });
    this.spectatorDisabled.add(player);
  }

  @EventHandler
  public void onPlayerTeleportEvent(final PlayerTeleportEvent event) {

    final Player player = event.getPlayer();
    final PlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(player)) {
      return;
    }

    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (!this.spectatorDisabled.contains(gamePlayer)) {
      return;
    }

    final GameMode mode = player.getGameMode();
    if (mode != GameMode.SPECTATOR) {
      return;
    }

    final TeleportCause cause = event.getCause();
    if (cause != TeleportCause.SPECTATE) {
      return;
    }

    event.setCancelled(true);
  }
}
