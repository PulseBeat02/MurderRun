/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.gadget.survivor.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.gadget.survivor.SurvivorGadget;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.MovementManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.resourcepack.sound.Sounds;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Item;

public final class KillerRewind extends SurvivorGadget {

  public KillerRewind() {
    super(
      "killer_rewind",
      GameProperties.KILLER_REWIND_COST,
      ItemFactory.createGadget(
        "killer_rewind",
        GameProperties.KILLER_REWIND_MATERIAL,
        Message.MURDERER_REWIND_NAME.build(),
        Message.MURDERER_REWIND_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final Location location = player.getLocation();
    final GamePlayerManager manager = game.getPlayerManager();
    final MovementManager movementManager = manager.getMovementManager();
    final GamePlayer closest = manager.getNearestKiller(location);
    if (closest == null) {
      return true;
    }

    if (!(closest instanceof final Killer killer)) {
      return true;
    }

    final long current = System.currentTimeMillis();
    final long last = killer.getKillerRewindCooldown();
    final long difference = current - last;
    if (difference < GameProperties.KILLER_REWIND_COOLDOWN) {
      return true;
    }

    final boolean successful = movementManager.handleRewind(killer);
    if (!successful) {
      return true;
    }
    item.remove();

    killer.setKillerRewindCooldown(current);
    killer.setFallDistance(0.0f);

    final Component msg = Message.REWIND_ACTIVATE.build();
    final PlayerAudience audience = killer.getAudience();
    audience.sendMessage(msg);
    audience.playSound(Sounds.REWIND);

    return false;
  }
}
