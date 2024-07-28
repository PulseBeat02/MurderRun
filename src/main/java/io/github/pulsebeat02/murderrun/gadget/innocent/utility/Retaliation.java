package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Retaliation extends MurderGadget {

  public Retaliation() {
    super(
        "retaliation",
        Material.GOLD_BLOCK,
        Locale.RETALIATION_TRAP_NAME.build(),
        Locale.RETALIATION_TRAP_NAME.build());
  }

  @Override
  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    super.onDropEvent(game, event);
  }
}
