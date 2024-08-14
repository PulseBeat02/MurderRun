package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class AllSeeingEye extends KillerGadget {

  public AllSeeingEye() {
    super(
        "all_seeing_eye",
        Material.ENDER_EYE,
        Locale.ALL_SEEING_EYE_TRAP_NAME.build(),
        Locale.ALL_SEEING_EYE_TRAP_LORE.build(),
        48);
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

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> player.setGameMode(GameMode.ADVENTURE), 20 * 7);
  }
}
