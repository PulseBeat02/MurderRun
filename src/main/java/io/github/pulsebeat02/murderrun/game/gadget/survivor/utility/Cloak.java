package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Cloak extends SurvivorGadget {

  public Cloak() {
    super(
        "cloak", Material.WHITE_BANNER, Message.CLOAK_NAME.build(), Message.CLOAK_LORE.build(), 32);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    manager.hideNameTagForAliveInnocents(GadgetConstants.CLOAK_DURATION);

    final Component message = Message.CLOAK_ACTIVATE.build();
    manager.sendMessageToAllSurvivors(message);
    manager.playSoundForAllParticipants(GadgetConstants.CLOAK_SOUND);

    return false;
  }
}
