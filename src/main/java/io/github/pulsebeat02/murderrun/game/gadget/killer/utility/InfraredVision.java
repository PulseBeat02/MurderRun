package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
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
      GameProperties.INFRARED_VISION_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final PlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToLivingSurvivors(innocent -> this.setSurvivorGlow(scheduler, innocent, player));

    manager.playSoundForAllParticipants(GameProperties.INFRARED_VISION_SOUND);

    return false;
  }

  private void setSurvivorGlow(final GameScheduler scheduler, final GamePlayer survivor, final GamePlayer killer) {
    final PlayerAudience audience = survivor.getAudience();
    final Component msg = Message.INFRARED_VISION_ACTIVATE.build();
    final MetadataManager metadata = killer.getMetadataManager();
    metadata.setEntityGlowing(scheduler, survivor, ChatColor.RED, GameProperties.INFRARED_VISION_DURATION);
    audience.sendMessage(msg);
  }
}
