package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Phantom extends KillerGadget {

  public Phantom() {
    super(
      "phantom",
      Material.PHANTOM_MEMBRANE,
      Message.PHANTOM_NAME.build(),
      Message.PHANTOM_LORE.build(),
      GameProperties.PHANTOM_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final Game game, final GamePlayer player, final Item item, final boolean remove) {
    super.onGadgetDrop(game, player, item, true);

    player.setAllowSpectatorTeleport(false);
    player.setGameMode(GameMode.SPECTATOR);

    final Location old = player.getLocation();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.setDefault(player, old), GameProperties.PHANTOM_DURATION);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.PHANTOM_SOUND);

    return false;
  }

  private void setDefault(final GamePlayer player, final Location location) {
    player.setAllowSpectatorTeleport(true);
    player.teleport(location);
    player.setGameMode(GameMode.SURVIVAL);
  }
}
