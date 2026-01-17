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

import static net.kyori.adventure.text.Component.empty;

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class MindControl extends SurvivorGadget {

  public MindControl(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "mind_control",
        properties.getMindControlCost(),
        ItemFactory.createGadget(
            "mind_control",
            properties.getMindControlMaterial(),
            Message.MIND_CONTROL_NAME.build(),
            Message.MIND_CONTROL_LORE.build()));
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final GamePlayer player = packet.getPlayer();
    final Item item = packet.getItem();
    final GamePlayerManager manager = game.getPlayerManager();
    final Location originLoc = player.getLocation();
    final GamePlayer nearest = manager.getNearestKiller(originLoc);
    if (nearest == null) {
      return true;
    }

    if (!(player instanceof final Survivor survivor)) {
      return true;
    }
    item.remove();

    survivor.setCanPickupCarPart(false);

    final Location location = nearest.getLocation();
    final Location origin = player.getLocation();
    final GameProperties properties = game.getProperties();
    final int duration = properties.getMindControlDuration();
    player.addPotionEffects(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1));
    player.setInvulnerable(true);
    player.teleport(location);

    final StrictPlayerReference reference = StrictPlayerReference.of(player);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(
        () -> this.applyMindControlEffects(player, nearest), 0L, 1L, duration, reference);
    scheduler.scheduleTask(() -> this.resetPlayer(survivor, origin), duration, reference);

    final String targetName = nearest.getDisplayName();
    final Component targetMsg = Message.MIND_CONTROL_ACTIVATE_SURVIVOR.build(targetName);
    final PlayerAudience audience1 = player.getAudience();
    audience1.showTitle(empty(), targetMsg);
    audience1.playSound(properties.getMindControlSound());

    final String name = player.getDisplayName();
    final Component msg = Message.MIND_CONTROL_ACTIVATE_KILLER.build(name);
    final PlayerAudience audience = nearest.getAudience();
    audience.sendMessage(msg);

    return false;
  }

  private void resetPlayer(final Survivor player, final Location location) {
    player.teleport(location);
    player.setInvulnerable(false);
    player.setCanPickupCarPart(true);
  }

  private void applyMindControlEffects(final GamePlayer player, final GamePlayer killer) {
    final Location location = player.getLocation();
    final Vector velocity = player.getVelocity();
    killer.teleport(location);
    killer.setVelocity(velocity);
  }
}
