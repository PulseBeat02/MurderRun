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
import io.github.pulsebeat02.murderrun.game.player.MetadataManager;
import io.github.pulsebeat02.murderrun.game.player.PlayerAudience;
import io.github.pulsebeat02.murderrun.game.player.PlayerManager;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.locale.Message;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;

public final class Chipped extends SurvivorGadget {

  public Chipped() {
    super("chipped", Material.GOLD_NUGGET, Message.CHIPPED_NAME.build(), Message.CHIPPED_LORE.build(), GameProperties.CHIPPED_COST);
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final PlayerManager manager = game.getPlayerManager();
    final MetadataManager metadata = player.getMetadataManager();
    final GameScheduler scheduler = game.getScheduler();
    this.setOtherSurvivorsGlowing(manager, metadata, scheduler);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.CHIPPED_SOUND);

    return false;
  }

  private void setOtherSurvivorsGlowing(final PlayerManager manager, final MetadataManager metadata, final GameScheduler scheduler) {
    manager.applyToLivingSurvivors(innocent ->
      metadata.setEntityGlowing(scheduler, innocent, ChatColor.GREEN, GameProperties.CHIPPED_DURATION)
    );
  }
}
