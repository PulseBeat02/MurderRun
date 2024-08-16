package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Cloak extends SurvivorGadget {

  public Cloak() {
    super(
        "cloak",
        Material.WHITE_BANNER,
        Locale.CLOAK_TRAP_NAME.build(),
        Locale.CLOAK_TRAP_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToAllLivingInnocents(GamePlayer::hideNameTag);

    final Component message = Locale.CLOAK_TRAP_ACTIVATE.build();
    manager.applyToAllLivingInnocents(innocent -> innocent.sendMessage(message));
    scheduler.scheduleTask(
        () -> manager.applyToAllLivingInnocents(GamePlayer::showNameTag), 7 * 20L);
  }
}
