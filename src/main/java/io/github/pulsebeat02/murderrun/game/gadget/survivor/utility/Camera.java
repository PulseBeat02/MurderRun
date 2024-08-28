package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.gadget.util.CameraGadget;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerDropItemEvent;

public final class Camera extends SurvivorGadget {

  private CameraGadget gadget;

  public Camera() {
    super(
        "camera", Material.OBSERVER, Message.CAMERA_NAME.build(), Message.CAMERA_LORE.build(), 48);
  }

  @Override
  public void onGadgetDrop(final Game game, final PlayerDropItemEvent event, final boolean remove) {
    if (this.gadget == null) {
      this.gadget = new CameraGadget(this);
    }
    this.gadget.handleCamera(game, event);
  }
}
