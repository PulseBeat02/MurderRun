package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.MurderGameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.reflect.NMSHandler;
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
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final MurderPlayerManager manager = game.getPlayerManager();
    manager.applyToAllInnocents(innocent -> this.sendGlowPacket(innocent, player));

    final MurderGameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(
        () -> manager.applyToAllInnocents(innocent -> this.removeGlowPacket(innocent, player)),
        5 * 20);
  }

  private void sendGlowPacket(final GamePlayer gamePlayer, final Player target) {
    gamePlayer.apply(player -> NMSHandler.NMS_UTILS.sendGlowPacket(player, target));
  }

  private void removeGlowPacket(final GamePlayer gamePlayer, final Player target) {
    gamePlayer.apply(player -> NMSHandler.NMS_UTILS.sendRemoveGlowPacket(player, target));
  }
}
