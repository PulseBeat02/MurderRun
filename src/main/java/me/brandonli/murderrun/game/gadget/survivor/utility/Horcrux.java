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

import java.util.Objects;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.death.DeathManager;
import me.brandonli.murderrun.game.player.death.PlayerDeathTask;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public final class Horcrux extends SurvivorGadget {

  public Horcrux(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "horcrux",
      properties.getHorcruxCost(),
      ItemFactory.createGadget("horcrux", properties.getHorcruxMaterial(), Message.HORCRUX_NAME.build(), Message.HORCRUX_LORE.build())
    );
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final Location itemLocation = item.getLocation();
    final Location respawnPoint = itemLocation.clone();
    final Location clone = respawnPoint.clone();
    item.setVelocity(new Vector(0, 0, 0));
    item.setInvulnerable(true);

    final Runnable task = () -> this.createDeathTask(item, player, clone);
    final PlayerDeathTask deathTask = new PlayerDeathTask(task, true);
    final DeathManager manager = player.getDeathManager();
    manager.addDeathTask(deathTask);

    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleParticleTaskUntilDeath(item, Color.BLACK);

    return false;
  }

  private void createDeathTask(final Item item, final GamePlayer player, final Location respawnPoint) {
    if (item.isValid()) {
      item.remove();
    }
    this.startSpectatorRespawn(player, respawnPoint.clone());
  }

  private void startSpectatorRespawn(final GamePlayer player, final Location respawnPoint) {
    final StrictPlayerReference ref = StrictPlayerReference.of(player);
    player.setGameMode(GameMode.SPECTATOR);
    final Game game = player.getGame();
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.startCountdown(player, respawnPoint), 0L, ref);
  }

  private void startCountdown(final GamePlayer player, final Location respawnPoint) {
    final PlayerAudience audience = player.getAudience();
    audience.sendMessage(Message.HORCRUX_ACTIVATE.build());
    final GameScheduler scheduler = player.getGame().getScheduler();
    final StrictPlayerReference ref = StrictPlayerReference.of(player);
    scheduler.scheduleTask(() -> this.executeFinalRespawn(player, respawnPoint), 10L, ref);
  }

  private void executeFinalRespawn(final GamePlayer player, final Location point) {
    final Location safeLocation = this.findSafeLocation(point);
    player.setGameMode(GameMode.SURVIVAL);
    player.teleport(safeLocation);
    player.setInvulnerable(true);

    final Game game = player.getGame();
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    scheduler.scheduleTask(() -> player.setInvulnerable(false), 3 * 20L, reference);
  }

  private Location findSafeLocation(final Location temp) {
    Location loc = temp.clone();
    final Block originBlock = loc.getBlock();
    Material blockType = originBlock.getType();
    if (!blockType.isAir()) {
      loc = loc.add(0, 1, 0);
      final Block block = loc.getBlock();
      blockType = block.getType();
      if (!blockType.isAir()) {
        final World world = Objects.requireNonNull(loc.getWorld());
        final Block highestBlock = world.getHighestBlockAt(loc);
        final Location highestBlockLocation = highestBlock.getLocation();
        loc = highestBlockLocation.add(0, 1, 0);
      }
    }
    loc = loc.add(0, 0.5, 0);
    return loc;
  }
}
