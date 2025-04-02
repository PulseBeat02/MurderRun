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
package io.github.pulsebeat02.murderrun.game.gadget.survivor.trap;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.player.GamePlayer;
import io.github.pulsebeat02.murderrun.game.player.GamePlayerManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import io.github.pulsebeat02.murderrun.utils.item.ItemFactory;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.inventory.HorseInventory;

public final class PonyTrap extends SurvivorTrap {

  public PonyTrap() {
    super(
      "pony",
      GameProperties.PONY_COST,
      ItemFactory.createGadget("pony", Material.SADDLE, Message.PONY_NAME.build(), Message.PONY_LORE.build()),
      Message.PONY_ACTIVATE.build(),
      new Color(177, 156, 217)
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final Location location = murderer.getLocation();
    this.spawnHorse(location);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.PONY_SOUND);
  }

  private void spawnHorse(final Location location) {
    final World world = requireNonNull(location.getWorld());
    world.spawn(location, Horse.class, horse -> {
      this.customizeProperties(horse);
      this.setSaddle(horse);
    });
  }

  private void customizeProperties(final Horse horse) {
    horse.setTamed(true);
    horse.setJumpStrength(2);
    horse.setAdult();
    this.setSpeed(horse);
  }

  private void setSpeed(final Horse horse) {
    final AttributeInstance attribute = requireNonNull(horse.getAttribute(Attribute.MOVEMENT_SPEED));
    attribute.setBaseValue(GameProperties.PONY_HORSE_SPEED);
  }

  private void setSaddle(final Horse horse) {
    final HorseInventory inventory = horse.getInventory();
    inventory.setSaddle(ItemFactory.createSaddle());
  }
}
