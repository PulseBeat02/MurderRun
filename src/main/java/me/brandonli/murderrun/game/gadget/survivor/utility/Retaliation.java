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

import java.util.stream.Stream;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
import me.brandonli.murderrun.game.gadget.survivor.SurvivorGadget;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Retaliation extends SurvivorGadget {

  public Retaliation(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
        "retaliation",
        properties.getRetaliationCost(),
        ItemFactory.createGadget(
            "retaliation",
            properties.getRetaliationMaterial(),
            Message.RETALIATION_NAME.build(),
            Message.RETALIATION_LORE.build()));
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
    scheduler.scheduleRepeatedTask(
        () -> this.checkForDeadPlayers(manager, player), 0, 4 * 20L, reference);

    final GameProperties properties = game.getProperties();
    final Component message = Message.RETALIATION_ACTIVATE.build();
    final PlayerAudience audience = player.getAudience();
    audience.sendMessage(message);
    audience.playSound(properties.getRetaliationSound());

    return false;
  }

  private void checkForDeadPlayers(final GamePlayerManager manager, final GamePlayer player) {
    final Stream<GamePlayer> deathCount = manager.getDeceasedSurvivors();
    final long dead = deathCount.count();
    if (dead == 0) {
      return;
    }

    final int level = (int) (dead - 1);
    final Game game = manager.getGame();
    final GameProperties properties = game.getProperties();
    final int effectLevel = Math.min(level, properties.getRetaliationMaxAmplifier());
    player.addPotionEffects(
        new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, effectLevel),
        new PotionEffect(
            PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, effectLevel),
        new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, effectLevel));
  }
}
