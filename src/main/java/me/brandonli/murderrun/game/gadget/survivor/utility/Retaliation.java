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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import java.util.stream.Stream;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Retaliation extends SurvivorGadget {

  public Retaliation() {
    super(
      "retaliation",
      GameProperties.RETALIATION_COST,
      ItemFactory.createGadget(
        "retaliation",
        GameProperties.RETALIATION_MATERIAL,
        Message.RETALIATION_NAME.build(),
        Message.RETALIATION_LORE.build()
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
    final NullReference reference = NullReference.of();
    scheduler.scheduleRepeatedTask(() -> this.checkForDeadPlayers(manager, player), 0, 4 * 20L, reference);

    final Component message = Message.RETALIATION_ACTIVATE.build();
    final PlayerAudience audience = player.getAudience();
    audience.sendMessage(message);
    audience.playSound(GameProperties.RETALIATION_SOUND);

    return false;
  }

  private void checkForDeadPlayers(final GamePlayerManager manager, final GamePlayer player) {
    final Stream<GamePlayer> deathCount = manager.getDeceasedSurvivors();
    final long dead = deathCount.count();
    if (dead == 0) {
      return;
    }

    final int level = (int) (dead - 1);
    final int effectLevel = Math.min(level, GameProperties.RETALIATION_MAX_AMPLIFIER);
    player.addPotionEffects(
      new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, effectLevel),
      new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, effectLevel),
      new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, effectLevel)
    );
  }
}
