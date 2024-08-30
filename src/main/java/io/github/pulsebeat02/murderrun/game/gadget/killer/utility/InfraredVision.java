package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.data.GadgetConstants;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;

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
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {

    super.onGadgetDrop(game, player, item, true);

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToAllLivingInnocents(
        innocent -> this.setSurvivorGlow(scheduler, innocent, player));

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GadgetConstants.INFRARED_VISION_SOUND);

    return false;
  }

  private void setSurvivorGlow(
      final GameScheduler scheduler, final GamePlayer survivor, final GamePlayer killer) {
    final PlayerAudience audience = survivor.getAudience();
    final Component msg = Message.INFRARED_VISION_ACTIVATE.build();
    final MetadataManager metadata = killer.getMetadataManager();
    metadata.setEntityGlowing(
        scheduler, survivor, ChatColor.RED, GadgetConstants.INFRARED_VISION_DURATION);
    audience.sendMessage(msg);
  }
}
