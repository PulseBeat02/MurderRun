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
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class WarpDistort extends KillerGadget {

  public WarpDistort() {
    super(
      "warp_distort",
      Material.CHORUS_FRUIT,
      Message.WARP_DISTORT_NAME.build(),
      Message.WARP_DISTORT_LORE.build(),
      GameProperties.WARP_DISTORT_COST
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final Item item = packet.getItem();

    final PlayerManager manager = game.getPlayerManager();
    final Stream<GamePlayer> survivors = manager.getLivingInnocentPlayers();
    final long size = survivors.count();
    if (size < 2) {
      return true;
    }
    item.remove();

    final GamePlayer[] players = this.getRandomPlayers(manager);
    final GamePlayer random = players[0];
    final GamePlayer random2 = players[1];

    final Location first = random.getLocation();
    final Location second = random2.getLocation();
    random.teleport(second);
    random2.teleport(first);

    final String sound = GameProperties.WARP_DISTORT_SOUND;
    final PlayerAudience randomAudience = random.getAudience();
    final PlayerAudience random2Audience = random2.getAudience();
    randomAudience.playSound(sound);
    random2Audience.playSound(sound);

    final Component msg = Message.WARP_DISTORT_ACTIVATE.build();
    randomAudience.sendMessage(msg);
    random2Audience.sendMessage(msg);

    return false;
  }

  private GamePlayer[] getRandomPlayers(final PlayerManager manager) {
    final GamePlayer random = manager.getRandomAliveInnocentPlayer();
    GamePlayer random2 = manager.getRandomAliveInnocentPlayer();
    while (random == random2) {
      random2 = manager.getRandomAliveInnocentPlayer();
    }
    return new GamePlayer[] { random, random2 };
  }
}
