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
package me.brandonli.murderrun.game.gadget.survivor.utility;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Cloak extends SurvivorGadget {

  public Cloak(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "cloak",
      properties.getCloakCost(),
      ItemFactory.createGadget("cloak", properties.getCloakMaterial(), Message.CLOAK_NAME.build(), Message.CLOAK_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final GameProperties properties = game.getProperties();
    final GamePlayerManager manager = game.getPlayerManager();
    manager.applyToLivingSurvivors(survivor ->
      survivor.addPotionEffects(new PotionEffect(PotionEffectType.INVISIBILITY, properties.getCloakDuration(), 0))
    );

    final Component message = Message.CLOAK_ACTIVATE.build();
    manager.sendMessageToAllLivingSurvivors(message);
    manager.playSoundForAllParticipants(properties.getCloakSound());

    return false;
  }
}
