package io.github.pulsebeat02.murderrun.game.gadget.innocent.utility;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.gadget.DeathTask;
import io.github.pulsebeat02.murderrun.game.gadget.MurderGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MurderPlayerManager;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Horcrux extends MurderGadget {

  public Horcrux() {
    super(
        "horcrux",
        Material.CHARCOAL,
        Locale.HORCRUX_TRAP_NAME.build(),
        Locale.HORCRUX_TRAP_LORE.build());
  }

  @Override
  public void onGadgetDrop(
      final MurderGame game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, false);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final MurderPlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.lookupPlayer(player).orElseThrow();
    final DeathTask task = new DeathTask(() -> this.handleHorcrux(gamePlayer, location), true);
    gamePlayer.addDeathTask(task);
  }

  private void handleHorcrux(final GamePlayer player, final Location location) {
    final Component message = Locale.HORCRUX_TRAP_ACTIVATE.build();
    player.teleport(location);
    player.sendMessage(message);
  }
}
