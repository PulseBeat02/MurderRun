package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.locale.Locale;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Gamble extends KillerGadget {

  public Gamble() {
    super(
        "gamble",
        Material.END_PORTAL_FRAME,
        Locale.GAMBLE_TRAP_NAME.build(),
        Locale.GAMBLE_TRAP_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);
  }
}
