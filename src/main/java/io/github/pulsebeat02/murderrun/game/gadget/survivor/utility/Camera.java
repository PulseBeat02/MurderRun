package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.misc.CameraGadget;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Camera extends SurvivorGadget {

  private CameraGadget gadget;

  public Camera() {
    super(
        "camera", Material.OBSERVER, Message.CAMERA_NAME.build(), Message.CAMERA_LORE.build(), 48);
  }

  @Override
  public boolean onGadgetDrop(
      final Game game, final GamePlayer player, final Item item, final boolean remove) {
    if (this.gadget == null) {
      this.gadget = new CameraGadget(this);
    }
    return this.gadget.handleCamera(game, player, item, remove);
  }
}
