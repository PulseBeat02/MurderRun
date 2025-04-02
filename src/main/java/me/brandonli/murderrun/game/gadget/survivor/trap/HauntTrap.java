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

import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class HauntTrap extends SurvivorTrap {

  public HauntTrap() {
    super(
      "haunt_trap",
      GameProperties.HAUNT_COST,
      ItemFactory.createGadget("haunt_trap", GameProperties.HAUNT_MATERIAL, Message.HAUNT_NAME.build(), Message.HAUNT_LORE.build()),
      Message.HAUNT_ACTIVATE.build(),
      GameProperties.HAUNT_COLOR
    );
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final Item item) {
    final int duration = GameProperties.HAUNT_DURATION;
    murderer.addPotionEffects(
      new PotionEffect(PotionEffectType.NAUSEA, duration, 10),
      new PotionEffect(PotionEffectType.BLINDNESS, duration, 1),
      new PotionEffect(PotionEffectType.SLOWNESS, duration, 4)
    );

    final StrictPlayerReference reference = StrictPlayerReference.of(murderer);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleRepeatedTask(() -> this.spook(game, murderer), 0, 20L, duration, reference);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants(GameProperties.HAUNT_SOUND);
  }

  private void spook(final Game game, final GamePlayer gamePlayer) {
    gamePlayer.addPotionEffects(new PotionEffect(PotionEffectType.DARKNESS, 20, 0));
    gamePlayer.spawnPlayerSpecificParticle(Particle.ELDER_GUARDIAN);

    final MetadataManager metadata = gamePlayer.getMetadataManager();
    metadata.setWorldBorderEffect(true);

    final StrictPlayerReference reference = StrictPlayerReference.of(gamePlayer);
    final GameScheduler scheduler = game.getScheduler();
    scheduler.scheduleTask(() -> this.unspook(gamePlayer), 19, reference);
  }

  private void unspook(final GamePlayer gamePlayer) {
    gamePlayer.removePotionEffect(PotionEffectType.DARKNESS);

    final MetadataManager metadata = gamePlayer.getMetadataManager();
    metadata.setWorldBorderEffect(false);
  }
}
