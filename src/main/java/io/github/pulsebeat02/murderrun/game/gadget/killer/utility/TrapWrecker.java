package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class TrapWrecker extends KillerGadget {

  public TrapWrecker() {
    super(
        "trap_wrecker",
        Material.BARRIER,
        Message.TRAP_WRECKER_NAME.build(),
        Message.TRAP_WRECKER_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final PlayerManager manager = game.getPlayerManager();
    final Player player = event.getPlayer();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    if (!(gamePlayer instanceof final Killer killer)) {
      return;
    }
    killer.setIgnoreTraps(true);

    final Component msg = Message.TRAP_WRECKER_ACTIVATE.build();
    killer.sendMessage(msg);
    killer.playSound("block.bone_block.break");

    final GameScheduler scheduler = game.getScheduler();
    final Consumer<Integer> consumer = (time) -> {
      if (time == 0) {
        killer.setIgnoreTraps(false);
      }
      killer.apply(raw -> raw.setLevel(time));
    };
    scheduler.scheduleCountdownTask(consumer, 30);
  }
}
