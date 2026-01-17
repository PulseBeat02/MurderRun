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

import static java.util.Objects.requireNonNull;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.packet.GadgetRightClickPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.inventory.meta.FireworkMeta;

public final class BlastOff extends SurvivorGadget {

  public BlastOff(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "blast_off",
        properties.getBlastOffCost(),
        ItemFactory.createGadget(
            "blast_off",
            properties.getBlastOffMaterial(),
            Message.BLAST_OFF_NAME.build(),
            Message.BLAST_OFF_LORE.build()));
  }

  @Override
  public boolean onGadgetRightClick(final GadgetRightClickPacket packet) {
    return true;
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();

    final Location location = player.getLocation();
    final GamePlayerManager manager = game.getPlayerManager();
    final GamePlayer killer = manager.getNearestKiller(location);
    if (killer == null) {
      return true;
    }
    item.remove();

    final Location before = killer.getLocation();
    final Firework firework = this.spawnRocket(killer);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleAfterDeath(() -> this.resetPlayer(killer, before), firework);
    killer.setInvulnerable(true);

    final StrictPlayerReference reference = StrictPlayerReference.of(killer);
    scheduler.scheduleTask(() -> player.setInvulnerable(false), 4 * 20L, reference);

    final GameProperties properties = game.getProperties();
    final PlayerAudience audience = player.getAudience();
    audience.playSound(properties.getBlastoffSound());

    return false;
  }

  private void resetPlayer(final GamePlayer killer, final Location before) {
    killer.teleport(before);
    killer.setFallDistance(0.0f);
    killer.setCanDismount(true);
  }

  private Firework spawnRocket(final GamePlayer player) {
    final Location location = player.getLocation();
    final World world = requireNonNull(location.getWorld());
    return world.spawn(location, Firework.class, firework -> {
      this.customizeMeta(firework);
      this.customizeProperties(player, firework);
      player.setCanDismount(false);
    });
  }

  private void customizeProperties(final GamePlayer player, final Firework firework) {
    player.apply(internal -> {
      firework.setShotAtAngle(false);
      firework.addPassenger(internal);
    });
  }

  private void customizeMeta(final Firework firework) {
    final FireworkMeta meta = firework.getFireworkMeta();
    meta.setPower(2);
    firework.setFireworkMeta(meta);
  }
}
