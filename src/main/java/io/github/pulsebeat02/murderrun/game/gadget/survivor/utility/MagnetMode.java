package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class MagnetMode extends SurvivorGadget {

  public MagnetMode() {
    super(
        "magnet_mode",
        Material.IRON_INGOT,
        Message.MAGNET_MODE_NAME.build(),
        Message.MAGNET_MODE_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GadgetManager gadgetManager = game.getGadgetManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final int current = gadgetManager.getActivationRange();
    gadgetManager.setActivationRange(current * 3);

    final Component message = Message.MAGNET_MODE_ACTIVATE.build();
    gamePlayer.sendMessage(message);
  }
}
