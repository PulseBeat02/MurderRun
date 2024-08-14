package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class MagnetMode extends SurvivorGadget {

  public MagnetMode() {
    super(
        "magnet_mode",
        Material.IRON_INGOT,
        Locale.MAGNET_MODE_TRAP_NAME.build(),
        Locale.MAGNET_MODE_TRAP_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GadgetManager gadgetManager = game.getGadgetManager();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final int current = gadgetManager.getActivationRange();
    gadgetManager.setActivationRange(current * 3);

    final Component message = Locale.MAGNET_MODE_TRAP_ACTIVATE.build();
    gamePlayer.sendMessage(message);
  }
}
