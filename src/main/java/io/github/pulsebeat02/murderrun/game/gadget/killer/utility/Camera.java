package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.misc.CameraGadget;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Camera extends KillerGadget {

  private CameraGadget gadget;

  public Camera() {
    super(
        "killer_camera",
        Material.OBSERVER,
        Message.KILLER_CAMERA_NAME.build(),
        Message.KILLER_CAMERA_LORE.build(),
        48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    if (this.gadget == null) {
      this.gadget = new CameraGadget(this);
    }
    this.gadget.handleCamera(game, event);
  }
}
