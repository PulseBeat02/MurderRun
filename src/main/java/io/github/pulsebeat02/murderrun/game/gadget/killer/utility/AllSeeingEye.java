package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
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

  private final Set<Player> spectatorDisabled;

  public AllSeeingEye() {
    super(
        "all_seeing_eye",
        Material.ENDER_EYE,
        Locale.ALL_SEEING_EYE_TRAP_NAME.build(),
        Locale.ALL_SEEING_EYE_TRAP_LORE.build(),
        48);
    this.spectatorDisabled = Collections.newSetFromMap(new WeakHashMap<>());
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final Survivor random = manager.getRandomAliveInnocentPlayer();
    random.apply(survivor -> {
      player.setGameMode(GameMode.SPECTATOR);
      player.setSpectatorTarget(survivor);
    });
    this.spectatorDisabled.add(player);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(
        () -> {
          player.setGameMode(GameMode.ADVENTURE);
          player.setSpectatorTarget(null);
          this.spectatorDisabled.remove(player);
        },
        20 * 7);
  }

  @EventHandler
  public void onPlayerTeleportEvent(final PlayerTeleportEvent event) {

    final Player player = event.getPlayer();
    if (!this.spectatorDisabled.contains(player)) {
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