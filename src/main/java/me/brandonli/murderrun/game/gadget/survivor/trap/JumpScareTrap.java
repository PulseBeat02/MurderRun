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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.resourcepack.sound.Sounds;
import me.brandonli.murderrun.utils.item.Item;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class JumpScareTrap extends SurvivorTrap {

  private final Set<GamePlayer> currentlyJumpScared;

  public JumpScareTrap(final Game game) {
    final GameProperties properties = game.getProperties();
    super(
      "jump_scare_trap",
      properties.getJumpScareCost(),
      ItemFactory.createGadget(
        "jump_scare_trap",
        properties.getJumpScareMaterial(),
        Message.JUMP_SCARE_NAME.build(),
        Message.JUMP_SCARE_LORE.build()
      ),
      Message.JUMP_SCARE_ACTIVATE.build(),
      properties.getJumpScareColor()
    );
    this.currentlyJumpScared = Collections.synchronizedSet(new HashSet<>());
  }

  @Override
  public void onTrapActivate(final Game game, final GamePlayer murderer, final org.bukkit.entity.Item item) {
    final GameProperties properties = game.getProperties();
    final int duration = properties.getJumpScareEffectDuration();
    murderer.addPotionEffects(
      new PotionEffect(PotionEffectType.BLINDNESS, duration, 1),
      new PotionEffect(PotionEffectType.SLOWNESS, duration, 1)
    );

    final PlayerAudience audience = murderer.getAudience();
    audience.playSound(Sounds.JUMP_SCARE);

    final GamePlayerManager manager = game.getPlayerManager();
    manager.playSoundForAllParticipants("entity.witch.celebrate");

    if (this.currentlyJumpScared.contains(murderer)) {
      return;
    }

    final ItemStack before = this.getHelmet(murderer);
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(murderer);
    scheduler.scheduleTask(() -> this.setBackHelmet(murderer, before), properties.getJumpScareDuration(), reference);
    this.currentlyJumpScared.add(murderer);
  }

  private void setBackHelmet(final GamePlayer player, final @Nullable ItemStack before) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(before);
    this.currentlyJumpScared.remove(player);
  }

  private @Nullable ItemStack getHelmet(final GamePlayer player) {
    final ItemStack stack = Item.create(Material.CARVED_PUMPKIN);
    final PlayerInventory inventory = player.getInventory();
    player.sendEquipmentChange(EquipmentSlot.HEAD, stack);
    return inventory.getHelmet();
  }
}
