package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Corruption extends KillerGadget {

  public Corruption() {
    super(
        "corruption",
        Material.ZOMBIE_HEAD,
        Message.CORRUPTION_NAME.build(),
        Message.CORRUPTION_LORE.build(),
        64);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);
  }
}
