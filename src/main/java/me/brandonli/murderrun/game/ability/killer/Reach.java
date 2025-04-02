/*

MIT License

Copyright (c) 2025 Brandon Li

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
package me.brandonli.murderrun.game.ability.killer;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

public final class Reach extends KillerAbility {

  public static final String REACH_NAME = "reach";

  public Reach(final Game game) {
    super(game, REACH_NAME, ItemFactory.createAbility(REACH_NAME, Message.REACH_NAME.build(), Message.REACH_LORE.build(), 1));
  }

  @Override
  public void start() {
    final Game game = this.getGame();
    final GamePlayerManager playerManager = game.getPlayerManager();
    final double reach = GameProperties.REACH_DISTANCE;
    playerManager.applyToLivingKillers(participant -> {
      if (!participant.hasAbility(REACH_NAME)) {
        return;
      }
      if (this.invokeEvent(participant)) {
        return;
      }
      final AttributeInstance instance = requireNonNull(participant.getAttribute(Attribute.BLOCK_INTERACTION_RANGE));
      instance.setBaseValue(reach);
    });
  }

  @Override
  public void shutdown() {
    final Game game = this.getGame();
    final GamePlayerManager playerManager = game.getPlayerManager();
    playerManager.applyToLivingKillers(participant -> {
      if (!participant.hasAbility(REACH_NAME)) {
        return;
      }
      participant.resetAttribute(Attribute.BLOCK_INTERACTION_RANGE);
    });
  }
}
