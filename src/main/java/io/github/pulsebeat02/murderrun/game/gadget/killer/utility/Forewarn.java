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
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.player.Survivor;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Forewarn extends KillerGadget {

  public Forewarn() {
    super("forewarn", Material.GLOWSTONE_DUST, Message.FOREWARN_NAME.build(), Message.FOREWARN_LORE.build(), GameProperties.FOREWARN_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final PlayerManager manager = game.getPlayerManager();
    if (!(player instanceof final Killer killer)) {
      return true;
    }
    item.remove();

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.handleInnocents(manager, killer), 0, 20L);

    final PlayerAudience audience = player.getAudience();
    final Component msg = Message.FOREWARN_ACTIVATE.build();
    audience.sendMessage(msg);
    audience.playSound(GameProperties.FOREWARN_SOUND);

    return false;
  }

  private void handleInnocents(final PlayerManager manager, final Killer gamePlayer) {
    manager.applyToLivingSurvivors(survivor -> this.handleForewarn(survivor, gamePlayer));
  }

  private void handleForewarn(final GamePlayer gamePlayer, final Killer player) {
    final Collection<GamePlayer> set = player.getForewarnGlowing();
    if (!(gamePlayer instanceof final Survivor survivor)) {
      return;
    }

    final MetadataManager metadata = player.getMetadataManager();
    if (survivor.hasCarPart()) {
      set.add(survivor);
      metadata.setEntityGlowing(survivor, ChatColor.RED, true);
    } else if (set.contains(survivor)) {
      set.remove(player);
      metadata.setEntityGlowing(survivor, ChatColor.RED, false);
    }
  }
}
