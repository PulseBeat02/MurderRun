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
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Parasite extends SurvivorGadget {

  private final Set<Integer> removed;

  public Parasite(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "parasite",
        properties.getParasiteCost(),
        ItemFactory.createGadget(
            "parasite",
            properties.getParsiteMaterial(),
            Message.PARASITE_NAME.build(),
            Message.PARASITE_LORE.build()));
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
    scheduler.scheduleParticleTaskUntilDeath(item, Color.GREEN);

    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getParasiteSound());

    return false;
  }

  private void handleKillers(final GamePlayerManager manager, final Item item) {
    manager.applyToKillers(killer -> this.checkActivationDistance(killer, manager, item));
  }

  private void checkActivationDistance(
      final GamePlayer player, final GamePlayerManager manager, final Item item) {
    final Location origin = item.getLocation();
    final Location location = player.getLocation();
    final double distance = origin.distanceSquared(location);
    final Game game = manager.getGame();
    final GameProperties properties = game.getProperties();
    final double destroyRadius = properties.getParasiteDestroyRadius();
    final double radius = properties.getParasiteRadius();
    final int id = item.getEntityId();
    if (distance < destroyRadius * destroyRadius && !this.removed.contains(id)) {
      final Component message = Message.PARASITE_DEACTIVATE.build();
      manager.sendMessageToAllLivingSurvivors(message);
      item.remove();
      this.removed.add(id);
    } else if (distance < radius * radius) {
      player.addPotionEffects(
          new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, 0),
          new PotionEffect(PotionEffectType.POISON, 10 * 20, 0),
          new PotionEffect(PotionEffectType.WEAKNESS, 10 * 20, 0));
    }
  }
}
