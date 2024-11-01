package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Cloak extends SurvivorGadget {

  public Cloak() {
    super("cloak", Material.WHITE_BANNER, Message.CLOAK_NAME.build(), Message.CLOAK_LORE.build(), GameProperties.CLOAK_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final PlayerManager manager = game.getPlayerManager();
    manager.hideNameTagForAliveInnocents(GameProperties.CLOAK_DURATION);

    final Component message = Message.CLOAK_ACTIVATE.build();
    manager.sendMessageToAllLivingSurvivors(message);
    manager.playSoundForAllParticipants(GameProperties.CLOAK_SOUND);

    return false;
  }
}
