package io.github.pulsebeat02.murderrun.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.reflect.NMSHandler;
import io.github.pulsebeat02.murderrun.scheduler.MurderGameScheduler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Chipped extends MurderGadget {

  public Chipped() {
    super(
        "chipped",
        Material.GOLD_NUGGET,
        Locale.CHIPPED_TRAP_NAME.build(),
        Locale.CHIPPED_TRAP_LORE.build());
  }

  @Override
  public void onDropEvent(final MurderGame game, final PlayerDropItemEvent event) {
    super.onDropEvent(game, event);
    final MurderPlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final MurderGameScheduler scheduler = game.getScheduler();
    manager.applyToAllInnocents(
        innocent -> NMSHandler.NMS_UTILS.sendGlowPacket(player, innocent.getPlayer()));
    scheduler.scheduleTask(
        () -> {
          manager.applyToAllInnocents(
              innocent -> NMSHandler.NMS_UTILS.sendRemoveGlowPacket(player, innocent.getPlayer()));
        },
        5 * 20);
  }
}
