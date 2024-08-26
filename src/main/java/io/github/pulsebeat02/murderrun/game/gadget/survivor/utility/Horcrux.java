package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Horcrux extends SurvivorGadget {

  public Horcrux() {
    super(
        "horcrux",
        Material.CHARCOAL,
        Message.HORCRUX_NAME.build(),
        Message.HORCRUX_LORE.build(),
        64);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer gamePlayer = manager.getGamePlayer(player);
    final GameScheduler scheduler = game.getScheduler();
    final Item item = event.getItemDrop();
    final DeathManager deathManager = gamePlayer.getDeathManager();
    final PlayerDeathTask task =
        new PlayerDeathTask(() -> this.handleHorcrux(gamePlayer, item), true);
    deathManager.addDeathTask(task);
    gamePlayer.playSound("block.furnace.fire_crackle");
    scheduler.scheduleParticleTask(item, Color.BLACK);
  }

  private void handleHorcrux(final GamePlayer player, final Item item) {
    final Component message = Message.HORCRUX_ACTIVATE.build();
    final Location location = item.getLocation();
    player.apply(raw -> raw.setRespawnLocation(location, true));
    player.sendMessage(message);
    item.remove();
  }
}
