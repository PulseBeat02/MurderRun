package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.misc.CameraGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Camera extends KillerGadget {

  private CameraGadget gadget;

  public Camera() {
    super(
      "killer_camera",
      Material.OBSERVER,
      Message.KILLER_CAMERA_NAME.build(),
      Message.KILLER_CAMERA_LORE.build(),
      GameProperties.KILLER_CAMERA_COST
    );
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
