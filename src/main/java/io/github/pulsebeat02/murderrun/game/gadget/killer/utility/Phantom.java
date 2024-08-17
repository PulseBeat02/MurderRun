package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Phantom extends KillerGadget {

  public Phantom() {
    super(
        "phantom",
        Material.PHANTOM_MEMBRANE,
        Message.PHANTOM_NAME.build(),
        Message.PHANTOM_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, true);

    final Player player = event.getPlayer();
    player.setGameMode(GameMode.SPECTATOR);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.setDefault(player), 15 * 20L);
  }

  private void setDefault(final Player player) {
    player.setGameMode(GameMode.ADVENTURE);
  }
}
