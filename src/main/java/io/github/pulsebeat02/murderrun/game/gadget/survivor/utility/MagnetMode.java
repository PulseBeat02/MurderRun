package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class MagnetMode extends SurvivorGadget {

  public MagnetMode() {
    super(
      "magnet_mode",
      Material.IRON_INGOT,
      Message.MAGNET_MODE_NAME.build(),
      Message.MAGNET_MODE_LORE.build(),
      GameProperties.MAGNET_MODE_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GadgetManager gadgetManager = game.getGadgetManager();
    final double current = gadgetManager.getActivationRange();
    gadgetManager.setActivationRange(current * GameProperties.MAGNET_MODE_MULTIPLIER);

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.MAGNET_MODE_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GameProperties.MAGNET_MODE_SOUND);

    return false;
  }
}
