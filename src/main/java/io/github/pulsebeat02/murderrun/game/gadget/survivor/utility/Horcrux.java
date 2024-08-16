package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.locale.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Horcrux extends SurvivorGadget {

  public Horcrux() {
    super(
        "horcrux",
        Material.CHARCOAL,
        Locale.HORCRUX_TRAP_NAME.build(),
        Locale.HORCRUX_TRAP_LORE.build(),
        64);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {

    super.onGadgetDrop(game, event, false);

    final Player player = event.getPlayer();
    final Location location = player.getLocation();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final PlayerDeathTask task =
        new PlayerDeathTask(() -> this.handleHorcrux(gamePlayer, location), true);
    gamePlayer.addDeathTask(task);
  }

  private void handleHorcrux(final GamePlayer player, final Location location) {
    final Component message = Locale.HORCRUX_TRAP_ACTIVATE.build();
    player.teleport(location);
    player.sendMessage(message);
  }
}
