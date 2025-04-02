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
package me.brandonli.murderrun.game.gadget.killer.utility;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;

public final class RedArrow extends KillerGadget {

  public RedArrow() {
    super(
      "red_arrow",
      GameProperties.RED_ARROW_COST,
      ItemFactory.createGadget(
        "red_arrow",
        GameProperties.RED_ARROW_MATERIAL,
        Message.RED_ARROW_NAME.build(),
        Message.RED_ARROW_LORE.build()
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
    scheduler.scheduleRepeatedTask(() -> this.handleSurvivors(manager), 0, GameProperties.RED_ARROW_DURATION, reference);

    final PlayerAudience audience = player.getAudience();
    final Component message = Message.RED_ARROW_ACTIVATE.build();
    audience.sendMessage(message);
    audience.playSound(GameProperties.RED_ARROW_SOUND);

    return false;
  }

  private void handleSurvivors(final GamePlayerManager manager) {
    manager.applyToLivingSurvivors(this::spawnParticleBeam);
  }

  private void spawnParticleBeam(final GamePlayer player) {
    final Location location = player.getLocation();
    final World world = location.getWorld();
    if (world == null) {
      return;
    }

    final double startY = location.getY();
    final double skyLimit = world.getMaxHeight();
    final double x = location.getX();
    final double z = location.getZ();

    for (double y = startY; y <= skyLimit; y += 1.0) {
      final Location particleLocation = new Location(world, x, y, z);
      world.spawnParticle(Particle.DUST, particleLocation, 1, new DustOptions(Color.RED, 4));
    }
  }
}
