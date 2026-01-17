/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.ability.killer;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.gadget.Gadget;
import me.brandonli.murderrun.game.gadget.GadgetLoadingMechanism;
import me.brandonli.murderrun.game.gadget.GadgetManager;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.death.DeathManager;
import me.brandonli.murderrun.game.player.death.PlayerDeathTask;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.inventory.PlayerInventory;

public final class Gamble extends KillerAbility {

  public static final String GAMBLE_NAME = "gamble";

  public Gamble(final Game game) {
    super(
        game,
        GAMBLE_NAME,
        ItemFactory.createAbility(
            GAMBLE_NAME, Message.GAMBLE_NAME.build(), Message.GAMBLE_LORE.build(), 1));
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
      playerManager.applyToLivingSurvivors(
          survivor -> this.applyGamble(mechanism, survivor, participant));
    });
  }

  private void applyGamble(
      final GadgetLoadingMechanism mechanism, final GamePlayer survivor, final GamePlayer killer) {
    final DeathManager manager = survivor.getDeathManager();
    final Gadget random = mechanism.getRandomKillerGadget();
    final PlayerDeathTask task =
        new PlayerDeathTask(() -> this.giveKillerItem(killer, random), false);
    manager.addDeathTask(task);
  }

  private void giveKillerItem(final GamePlayer killer, final Gadget gadget) {
    final Item.Builder stack = gadget.getStackBuilder();
    final PlayerInventory inventory = killer.getInventory();
    inventory.addItem(stack.build());
  }
}
