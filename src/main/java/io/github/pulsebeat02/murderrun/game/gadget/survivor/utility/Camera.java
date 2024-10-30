package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.misc.CameraGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Camera extends SurvivorGadget {

  private CameraGadget gadget;

  public Camera() {
    super("camera", Material.OBSERVER, Message.CAMERA_NAME.build(), Message.CAMERA_LORE.build(), GameProperties.CAMERA_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    if (this.gadget == null) {
      this.gadget = new CameraGadget();
    }
    return this.gadget.handleCamera(game, player, item);
  }
}
