package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.PlayerManager;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import io.github.pulsebeat02.murderrun.utils.SchedulingUtils;
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
  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    final PlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(PlayerUtils::hideNameTag);
    SchedulingUtils.scheduleTask(() -> manager.applyToAllInnocents(PlayerUtils::showNameTag), 7 * 20);
  }
}
