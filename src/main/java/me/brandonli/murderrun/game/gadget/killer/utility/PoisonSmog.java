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

import static java.util.Objects.requireNonNull;

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
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PoisonSmog extends KillerGadget {

  public PoisonSmog(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "poison_smog",
      properties.getPoisonSmogCost(),
      ItemFactory.createGadget(
        "poison_smog",
        properties.getPosionSmogMaterial(),
        Message.POISON_SMOG_NAME.build(),
        Message.POISON_SMOG_LORE.build()
      )
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    item.remove();

    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    final GameScheduler scheduler = game.getScheduler();
    final GamePlayerManager manager = game.getPlayerManager();
    final NullReference reference = NullReference.of();
    final GameProperties properties = game.getProperties();
    scheduler.scheduleRepeatedTask(
      () -> this.handleSmog(world, location, manager),
      0,
      2 * 20L,
      properties.getPoisonSmogDuration(),
      reference
    );

    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getPoisonSmogSound());

    return false;
  }

  private void handleSmog(final World world, final Location location, final GamePlayerManager manager) {
    this.spawnSmogParticles(world, location);
    this.handleSurvivors(manager, location);
  }

  private void handleSurvivors(final GamePlayerManager manager, final Location origin) {
    manager.applyToLivingSurvivors(survivor -> this.handleDebuffs(survivor, origin));
  }

  private void handleDebuffs(final GamePlayer survivor, final Location origin) {
    final Location location = survivor.getLocation();
    final double distance = location.distanceSquared(origin);
    final Game game = survivor.getGame();
    final GameProperties properties = game.getProperties();
    final double radius = properties.getTrapSeekerRadius();
    if (distance < radius * radius) {
      survivor.addPotionEffects(new PotionEffect(PotionEffectType.POISON, 3 * 20, 0));
    }
  }

  private void spawnSmogParticles(final World world, final Location origin) {
    world.spawnParticle(Particle.DUST, origin, 25, 10, 10, 10, new DustOptions(Color.GREEN, 4));
  }
}
