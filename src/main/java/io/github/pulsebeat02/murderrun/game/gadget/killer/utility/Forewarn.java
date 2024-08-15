package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Forewarn extends KillerGadget {

  public Forewarn() {
    super(
        "forewarn",
        Material.GLOWSTONE_DUST,
        Locale.FOREWARN_TRAP_NAME.build(),
        Locale.FOREWARN_TRAP_LORE.build(),
        96);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final Component msg = Locale.FOREWARN_ACTIVATE.build();
    gamePlayer.sendMessage(msg);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> {
          manager.applyToAllInnocents(survivor -> {
            if (survivor.hasCarPart()) {}
          });
        },
        0,
        60);
  }

  private void handleForewarn(final Survivor survivor, final GamePlayer player) {
    if (survivor.hasCarPart()) {
      // PacketToolsProvider.INSTANCE.sendGlowPacket();
    }
  }
}
