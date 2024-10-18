package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.MovementManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Rewind extends SurvivorGadget {

  public Rewind() {
    super("rewind", Material.DIAMOND, Message.REWIND_NAME.build(), Message.REWIND_LORE.build(), GameProperties.REWIND_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final PlayerManager manager = game.getPlayerManager();
    final MovementManager movementManager = manager.getMovementManager();
    if (!(player instanceof final Survivor survivor)) {
      return true;
    }

    final long current = System.currentTimeMillis();
    final long last = survivor.getRewindCooldown();
    if (current - last < GameProperties.REWIND_COOLDOWN) {
      return true;
    }

    final boolean successful = movementManager.handleRewind(player);
    if (!successful) {
      return true;
    }
    item.remove();

    survivor.setRewindCooldown(current);
    survivor.setFallDistance(0.0f);

    final Component msg = Message.REWIND_ACTIVATE.build();
    final PlayerAudience audience = survivor.getAudience();
    audience.sendMessage(msg);
    audience.playSound(Sounds.REWIND);

    return false;
  }
}
