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
import static net.kyori.adventure.text.Component.text;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.RandomUtils;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Sheep;

public final class JebTrap extends SurvivorTrap {

  public JebTrap(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "jeb_trap",
      properties.getJebCost(),
      ItemFactory.createGadget("jeb_trap", properties.getJebMaterial(), Message.JEB_NAME.build(), Message.JEB_LORE.build()),
      Message.JEB_ACTIVATE.build(),
      properties.getJebColor()
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final Location location = murderer.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GameProperties properties = game.getProperties();
    final Component component = text("jeb_");
    for (int i = 0; i < properties.getJebSheepCount(); i++) {
      world.spawn(location, Sheep.class, sheep -> sheep.customName(component));
    }

    final NullReference reference = NullReference.of();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spawnRainbowParticles(location), 0, 5, properties.getJebDuration(), reference);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(properties.getJebSound());
  }

  private void spawnRainbowParticles(final Location location) {
    final World world = requireNonNull(location.getWorld());
    final int r = RandomUtils.generateInt(255);
    final int g = RandomUtils.generateInt(255);
    final int b = RandomUtils.generateInt(255);
    final org.bukkit.Color color = org.bukkit.Color.fromRGB(r, g, b);
    world.spawnParticle(Particle.DUST, location, 15, 3, 3, 3, new DustOptions(color, 4));
  }
}
