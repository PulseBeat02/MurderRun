package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Cloak extends MurderGadget {

  public Cloak() {
    super(
        "cloak",
        Material.WHITE_BANNER,
        Locale.CLOAK_TRAP_NAME.build(),
        Locale.CLOAK_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {
    super.onDropEvent(game, event, true);
    final MurderPlayerManager manager = game.getPlayerManager();
    final Component message = Locale.CLOAK_TRAP_ACTIVATE.build();
    final MurderGameScheduler scheduler = game.getScheduler();
    manager.applyToAllInnocents(PlayerUtils::hideNameTag);
    manager.applyToAllInnocents(innocent -> innocent.sendMessage(message));
    scheduler.scheduleTask(() -> manager.applyToAllInnocents(PlayerUtils::showNameTag), 7 * 20);
  }
}
