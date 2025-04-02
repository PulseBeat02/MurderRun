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

import java.util.HashSet;
import java.util.Set;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;

public final class Distorter extends SurvivorGadget {

  private final Set<Integer> removed;

  public Distorter() {
    super(
      "distorter",
      GameProperties.DISTORTER_COST,
      ItemFactory.createGadget(
        "distorter",
        GameProperties.DISTORTER_MATERIAL,
        Message.DISTORTER_NAME.build(),
        Message.DISTORTER_LORE.build()
      )
    );
    this.removed = new HashSet<>();
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    final GamePlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTaskUntilDeath(() -> this.handleKillers(manager, item), item);
    scheduler.scheduleParticleTaskUntilDeath(item, Color.PURPLE);

    final PlayerAudience audience = player.getAudience();
    audience.playSound(GameProperties.DISTORTER_SOUND);

    return false;
  }

  private void handleKillers(final GamePlayerManager manager, final Item item) {
    manager.applyToKillers(killer -> this.applyDistortionEffect(manager, killer, item));
  }

  private void applyDistortionEffect(final GamePlayerManager manager, final GamePlayer killer, final Item item) {
    final Location location = killer.getLocation();
    final Location origin = item.getLocation();
    final double distance = location.distanceSquared(origin);
    final double destroyRadius = GameProperties.DISTORTER_DESTROY_RADIUS;
    final double effectRadius = GameProperties.DISTORTER_EFFECT_RADIUS;
    final int id = item.getEntityId();
    if (distance < destroyRadius * destroyRadius && !this.removed.contains(id)) {
      final Component message = Message.DISTORTER_DEACTIVATE.build();
      manager.sendMessageToAllLivingSurvivors(message);
      item.remove();
      this.removed.add(id);
    } else if (distance < effectRadius * effectRadius) {
      killer.spawnPlayerSpecificParticle(Particle.ELDER_GUARDIAN);
    }
  }
}
