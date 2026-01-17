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
package me.brandonli.murderrun.game.ability.survivor;

import static java.util.Objects.requireNonNull;

import java.util.List;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.GameSettings;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.gadget.Gadget;
import me.brandonli.murderrun.game.gadget.GadgetLoadingMechanism;
import me.brandonli.murderrun.game.gadget.GadgetManager;
import me.brandonli.murderrun.game.gadget.survivor.trap.SurvivorTrap;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.death.DeathManager;
import me.brandonli.murderrun.game.player.death.PlayerDeathTask;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.PDCUtils;
import me.brandonli.murderrun.utils.RandomUtils;
import me.brandonli.murderrun.utils.item.ItemFactory;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class TrapVest extends SurvivorAbility {

  private static final String TRAP_VEST_NAME = "trap_vest";

  public TrapVest(final Game game) {
    super(
        game,
        TRAP_VEST_NAME,
        ItemFactory.createAbility(
            TRAP_VEST_NAME, Message.TRAP_VEST_NAME.build(), Message.TRAP_VEST_LORE.build(), 1));
  }

  @Override
  public void start() {
    final Game game = this.getGame();
    final GamePlayerManager manager = game.getPlayerManager();
    manager.applyToLivingSurvivors(participant -> {
      if (!participant.hasAbility(TRAP_VEST_NAME)) {
        return;
      }
      if (this.invokeEvent(participant)) {
        return;
      }
      final DeathManager deathManager = participant.getDeathManager();
      final PlayerDeathTask task =
          new PlayerDeathTask(() -> this.handleTrapVest(participant), false);
      deathManager.addDeathTask(task);
    });
  }

  private void handleTrapVest(final GamePlayer gamePlayer) {
    this.teleport(gamePlayer);
    this.handleTraps(gamePlayer);
  }

  private void teleport(final GamePlayer gamePlayer) {
    final Game game = this.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final Location location = arena.getSpawn();
    gamePlayer.setRespawnLocation(location, true);
  }

  private void handleTraps(final GamePlayer player) {
    final Game game = this.getGame();
    final GadgetManager manager = game.getGadgetManager();
    final Location location = requireNonNull(player.getDeathLocation());
    final DeathManager deathManager = player.getDeathManager();
    final List<ItemStack> drops = deathManager.getDeathLoot();
    final GadgetLoadingMechanism mechanism = manager.getMechanism();
    final GameProperties properties = game.getProperties();
    final double multiplier = properties.getTrapVestVelocity();
    final World world = requireNonNull(location.getWorld());
    for (final ItemStack slot : drops) {
      if (slot == null) {
        continue;
      }
      if (PDCUtils.isAbility(slot)) {
        continue;
      }
      final int count = slot.getAmount();
      for (int i = 0; i < count; i++) {
        final Item droppedItem = world.dropItem(location, slot);
        final ItemStack stack = droppedItem.getItemStack();
        final Gadget gadget = mechanism.getGadgetFromStack(stack);
        if (gadget instanceof SurvivorTrap) {
          final Vector velocity = new Vector(
              (RandomUtils.generateDouble() - 0.5) * multiplier,
              RandomUtils.generateDouble() * multiplier,
              (RandomUtils.generateDouble() - 0.5) * multiplier);
          droppedItem.setVelocity(velocity);
        }
      }
    }
  }
}
