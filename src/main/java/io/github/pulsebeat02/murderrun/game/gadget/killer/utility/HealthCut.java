package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class HealthCut extends KillerGadget {

  public HealthCut() {
    super(
        "health_cut",
        Material.GOLDEN_SWORD,
        Message.HEALTH_CUT_NAME.build(),
        Message.HEALTH_CUT_LORE.build(),
        GameProperties.HEALTH_CUT_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToAllLivingInnocents(survivor -> this.setState(survivor, scheduler));

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.HEALTH_CUT_SOUND);

    return false;
  }

  private void setState(final GamePlayer survivor, final GameScheduler scheduler) {
    final PlayerAudience audience = survivor.getAudience();
    final Component msg = Message.HEALTH_CUT_ACTIVATE.build();
    audience.sendMessage(msg);
    this.resetState(survivor, scheduler);
  }

  private void resetState(final GamePlayer survivor, final GameScheduler scheduler) {
    final double before = survivor.getHealth();
    survivor.setHealth(2d);
    scheduler.scheduleTask(() -> survivor.setHealth(before), 5 * 20L);
  }
}
