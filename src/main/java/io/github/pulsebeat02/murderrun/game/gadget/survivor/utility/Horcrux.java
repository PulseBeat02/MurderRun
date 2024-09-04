package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Horcrux extends SurvivorGadget {

  public Horcrux() {
    super(
        "horcrux",
        Material.CHARCOAL,
        Message.HORCRUX_NAME.build(),
        Message.HORCRUX_LORE.build(),
        GameProperties.HORCRUX_COST);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    final DeathManager deathManager = player.getDeathManager();
    final PlayerDeathTask task = new PlayerDeathTask(() -> this.handleHorcrux(player, item), true);
    deathManager.addDeathTask(task);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleParticleTaskUntilDeath(item, Color.BLACK);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.HORCRUX_SOUND);

    return false;
  }

  private void handleHorcrux(final GamePlayer player, final Item item) {

    final Location location = item.getLocation();
    player.setRespawnLocation(location, true);
    player.teleport(location);
    item.remove();

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.HORCRUX_ACTIVATE.build();
    audience.sendMessage(message);
  }
}
