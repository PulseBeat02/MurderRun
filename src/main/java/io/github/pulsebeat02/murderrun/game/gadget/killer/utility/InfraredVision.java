package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Locale;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class InfraredVision extends KillerGadget {

  public InfraredVision() {
    super(
        "infrared_vision",
        Material.REDSTONE_LAMP,
        Locale.INFRARED_VISION_TRAP_NAME.build(),
        Locale.INFRARED_VISION_TRAP_LORE.build(),
        16);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final Component msg = Locale.INFRARED_VISION_ACTIVATE.build();
    manager.applyToAllInnocents(innocent -> {
      innocent.apply(survivor -> PacketToolsProvider.INSTANCE.sendGlowPacket(player, survivor));
      innocent.sendMessage(msg);
    });

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.removeGlow(player, manager), 20 * 7);
  }

  private void removeGlow(final Player watcher, final PlayerManager manager) {
    manager.applyToAllInnocents(innocent -> innocent.apply(
        survivor -> PacketToolsProvider.INSTANCE.sendRemoveGlowPacket(watcher, survivor)));
  }
}
