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
package me.brandonli.murderrun.game.gadget.killer.utility;

import static net.kyori.adventure.text.Component.empty;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;

public final class InfraredVision extends KillerGadget {

  public InfraredVision() {
    super(
      "infrared_vision",
      GameProperties.INFRARED_VISION_COST,
      ItemFactory.createGadget(
        "infrared_vision",
        GameProperties.INFRARED_VISION_MATERIAL,
        Message.INFRARED_VISION_NAME.build(),
        Message.INFRARED_VISION_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToLivingSurvivors(innocent -> this.setSurvivorGlow(scheduler, innocent, player));
    manager.playSoundForAllParticipants(GameProperties.INFRARED_VISION_SOUND);

    return false;
  }

  private void setSurvivorGlow(final GameScheduler scheduler, final GamePlayer survivor, final GamePlayer killer) {
    final PlayerAudience audience = survivor.getAudience();
    final Component msg = Message.INFRARED_VISION_ACTIVATE.build();
    final Component title = Message.INFRARED_VISION_ACTIVATE_TITLE.build();
    final MetadataManager metadata = killer.getMetadataManager();
    metadata.setEntityGlowing(scheduler, survivor, ChatColor.RED, GameProperties.INFRARED_VISION_DURATION);
    audience.sendMessage(msg);
    audience.showTitle(empty(), title);
  }
}
