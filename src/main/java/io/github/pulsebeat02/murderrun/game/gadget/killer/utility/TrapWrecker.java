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
package io.github.pulsebeat02.murderrun.game.gadget.killer.utility;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.gadget.killer.KillerGadget;
import io.github.pulsebeat02.murderrun.game.gadget.packet.GadgetDropPacket;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.Killer;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.game.scheduler.reference.StrictPlayerReference;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Item;

public final class TrapWrecker extends KillerGadget {

  public TrapWrecker() {
    super(
      "trap_wrecker",
      GameProperties.TRAP_WRECKER_COST,
      ItemFactory.createGadget(
        "trap_wrecker",
        GameProperties.TRAP_WRECKER_MATERIAL,
        Message.TRAP_WRECKER_NAME.build(),
        Message.TRAP_WRECKER_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    if (!(player instanceof final Killer killer)) {
      return true;
    }
    item.remove();

    killer.setIgnoreTraps(true);

    final GameScheduler scheduler = game.getScheduler();
    final Consumer<Integer> consumer = time -> {
      if (time == 0) {
        killer.setIgnoreTraps(false);
      }
      killer.setLevel(time);
    };
    final StrictPlayerReference reference = StrictPlayerReference.of(killer);
    scheduler.scheduleCountdownTask(consumer, GameProperties.TRAP_WRECKER_DURATION, reference);

    final PlayerAudience audience = killer.getAudience();
    final Component msg = Message.TRAP_WRECKER_ACTIVATE.build();
    audience.sendMessage(msg);
    audience.playSound(GameProperties.TRAP_WRECKER_SOUND);

    return false;
  }
}
