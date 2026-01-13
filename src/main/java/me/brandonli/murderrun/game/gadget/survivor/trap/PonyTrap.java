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
package me.brandonli.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.inventory.HorseInventory;

public final class PonyTrap extends SurvivorTrap {

  public PonyTrap(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "pony_trap",
      properties.getPonyCost(),
      ItemFactory.createGadget("pony_trap", properties.getPonyMaterial(), Message.PONY_NAME.build(), Message.PONY_LORE.build()),
      Message.PONY_ACTIVATE.build(),
      properties.getPonyColor()
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final Location location = murderer.getLocation();
    this.spawnHorse(game, location);

    final GameProperties properties = game.getProperties();
    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(properties.getPonySound());
  }

  private void spawnHorse(final Game game, final Location location) {
    final World world = requireNonNull(location.getWorld());
    world.spawn(location, Horse.class, horse -> {
      this.customizeProperties(game, horse);
      this.setSaddle(horse);
    });
  }

  private void customizeProperties(final Game game, final Horse horse) {
    horse.setTamed(true);
    horse.setJumpStrength(2);
    horse.setAdult();
    this.setSpeed(game, horse);
  }

  private void setSpeed(final Game game, final Horse horse) {
    final GameProperties properties = game.getProperties();
    final AttributeInstance attribute = requireNonNull(horse.getAttribute(Attribute.MOVEMENT_SPEED));
    attribute.setBaseValue(properties.getPonyHorseSpeed());
  }

  private void setSaddle(final Horse horse) {
    final HorseInventory inventory = horse.getInventory();
    inventory.setSaddle(ItemFactory.createSaddle());
  }
}
