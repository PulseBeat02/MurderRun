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

import java.util.*;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.gadget.killer.KillerGadget;
import me.brandonli.murderrun.game.gadget.packet.GadgetDropPacket;
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

public final class Fright extends KillerGadget {

  private final Set<GamePlayer> currentlyJumpScared;

  public Fright() {
    super(
      "fright",
      GameProperties.FRIGHT_COST,
      ItemFactory.createGadget("fright", GameProperties.FRIGHT_MATERIAL, Message.FRIGHT_NAME.build(), Message.FRIGHT_LORE.build())
    );
    this.currentlyJumpScared = Collections.synchronizedSet(new HashSet<>());
  }

  @Override
  public boolean onGadgetDrop(final GadgetDropPacket packet) {
    final Game game = packet.getGame();
    final org.bukkit.entity.Item item = packet.getItem();
    item.remove();

    final GamePlayerManager manager = game.getPlayerManager();
    final GameScheduler scheduler = game.getScheduler();
    manager.applyToLivingSurvivors(survivor -> this.jumpScareSurvivor(survivor, scheduler));

    return false;
  }

  private void jumpScareSurvivor(final GamePlayer survivor, final GameScheduler scheduler) {
    final ItemStack before = this.setPumpkinItemStack(survivor);
    final int duration = GameProperties.FRIGHT_DURATION;
    survivor.addPotionEffects(
      new PotionEffect(PotionEffectType.BLINDNESS, duration, 1),
      new PotionEffect(PotionEffectType.SLOWNESS, duration, 1)
    );

    final PlayerAudience audience = survivor.getAudience();
    audience.playSound(Sounds.JUMP_SCARE);

    if (this.currentlyJumpScared.contains(survivor)) {
      return;
    }

    final StrictPlayerReference reference = StrictPlayerReference.of(survivor);
    scheduler.scheduleTask(() -> this.setBackHelmet(survivor, before), 2 * 20L, reference);
    this.currentlyJumpScared.add(survivor);
  }

  private void setBackHelmet(final GamePlayer player, final @Nullable ItemStack before) {
    final PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(before);
    this.currentlyJumpScared.remove(player);
  }

  private @Nullable ItemStack setPumpkinItemStack(final GamePlayer player) {
    final ItemStack stack = Item.create(Material.CARVED_PUMPKIN);
    final PlayerInventory inventory = player.getInventory();
    player.sendEquipmentChange(EquipmentSlot.HEAD, stack);
    return inventory.getHelmet();
  }
}
