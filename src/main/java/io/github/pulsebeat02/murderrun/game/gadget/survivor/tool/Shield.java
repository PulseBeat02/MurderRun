package io.github.pulsebeat02.murderrun.game.gadget.survivor.tool;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

public final class Shield extends SurvivorGadget {

  public Shield() {
    super(
        "shield",
        Material.SHIELD,
        Message.SHIELD_NAME.build(),
        Message.SHIELD_LORE.build(),
        16,
        ItemFactory::createShield);
  }

  @Override
  public void onGadgetRightClick(
      final Game game, final PlayerInteractEvent event, final boolean remove) {
    // allow right click
  }
}
