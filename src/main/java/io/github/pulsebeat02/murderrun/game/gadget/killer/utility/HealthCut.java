package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class HealthCut extends KillerGadget {

  public HealthCut() {
    super(
        "health_cut",
        Material.GOLDEN_SWORD,
        Locale.HEALTH_CUT_ACTIVATE.build(),
        Locale.HEALTH_CUT_TRAP_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToAllLivingInnocents(survivor -> this.setState(survivor, scheduler));
  }

  private void setState(final GamePlayer survivor, final GameScheduler scheduler) {
    final Component msg = Locale.HEALTH_CUT_ACTIVATE.build();
    survivor.sendMessage(msg);
    this.resetState(survivor, scheduler);
  }

  private void resetState(final GamePlayer survivor, final GameScheduler scheduler) {
    survivor.apply(player -> {
      final double before = player.getHealth();
      player.setHealth(2d);
      scheduler.scheduleTask(() -> player.setHealth(before), 5 * 20L);
    });
  }
}
