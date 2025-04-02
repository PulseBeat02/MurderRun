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
package io.github.pulsebeat02.murderrun.game.ability.killer;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.gadget.Gadget;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetLoadingMechanism;
import io.github.pulsebeat02.murderrun.game.gadget.GadgetManager;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.game.player.death.DeathManager;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import org.bukkit.inventory.PlayerInventory;

public final class Gamble extends KillerAbility {

  public static final String GAMBLE_NAME = "gamble";

  public Gamble(final Game game) {
    super(game, GAMBLE_NAME, ItemFactory.createAbility(GAMBLE_NAME, Message.GAMBLE_NAME.build(), Message.GAMBLE_LORE.build(), 1));
  }

  @Override
  public void start() {
    final Game game = this.getGame();
    final GamePlayerManager playerManager = game.getPlayerManager();
    final GadgetManager manager = game.getGadgetManager();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    playerManager.applyToLivingKillers(participant -> {
      if (!participant.hasAbility(GAMBLE_NAME)) {
        return;
      }
      if (this.invokeEvent(participant)) {
        return;
      }
      playerManager.applyToLivingSurvivors(survivor -> this.applyGamble(mechanism, survivor, participant));
    });
  }

  private void applyGamble(final GadgetLoadingMechanism mechanism, final GamePlayer survivor, final GamePlayer killer) {
    final DeathManager manager = survivor.getDeathManager();
    final Gadget random = mechanism.getRandomKillerGadget();
    final PlayerDeathTask task = new PlayerDeathTask(() -> this.giveKillerItem(killer, random), false);
    manager.addDeathTask(task);
  }

  private void giveKillerItem(final GamePlayer killer, final Gadget gadget) {
    final io.github.pulsebeat02.murderrun.utils.item.Item.Builder stack = gadget.getStackBuilder();
    final PlayerInventory inventory = killer.getInventory();
    inventory.addItem(stack.build());
  }
}
