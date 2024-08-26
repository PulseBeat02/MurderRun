package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class InfraredVision extends KillerGadget {

  public InfraredVision() {
    super(
        "infrared_vision",
        Material.REDSTONE_LAMP,
        Message.INFRARED_VISION_NAME.build(),
        Message.INFRARED_VISION_LORE.build(),
        32);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    super.onGadgetDrop(game, event, true);
    final Player player = event.getPlayer();
    final PlayerManager manager = game.getPlayerManager();
    final GamePlayer killer = manager.getGamePlayer(player);
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToAllLivingInnocents(
        innocent -> this.setSurvivorGlow(scheduler, innocent, killer));
  }

  private void setSurvivorGlow(
      final GameScheduler scheduler, final GamePlayer survivor, final GamePlayer killer) {
    final Component msg = Message.INFRARED_VISION_ACTIVATE.build();
    final MetadataManager metadata = killer.getMetadataManager();
    metadata.setEntityGlowing(scheduler, survivor, ChatColor.RED, 7 * 20L);
    survivor.sendMessage(msg);
  }
}
