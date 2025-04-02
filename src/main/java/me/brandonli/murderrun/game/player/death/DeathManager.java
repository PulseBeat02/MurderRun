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
package me.brandonli.murderrun.game.player.death;

import java.util.*;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DeathManager {

  private @Nullable NPC corpse;
  private final List<PlayerDeathTask> tasks;
  private final List<ItemStack> deathLoot;

  public DeathManager() {
    this.tasks = new ArrayList<>();
    this.deathLoot = new ArrayList<>();
  }

  public boolean checkDeathCancellation() {
    for (final PlayerDeathTask task : this.tasks) {
      final boolean cancel = task.isCancelDeath();
      if (cancel) {
        return true;
      }
    }
    return false;
  }

  public void runDeathTasks() {
    final Iterator<PlayerDeathTask> iterator = this.tasks.iterator();
    while (iterator.hasNext()) {
      final PlayerDeathTask task = iterator.next();
      task.run();
      iterator.remove();
    }
  }

  public void addDeathTask(final PlayerDeathTask task) {
    this.tasks.add(task);
  }

  public void removeDeathTask(final PlayerDeathTask task) {
    this.tasks.remove(task);
  }

  public List<PlayerDeathTask> getDeathTasks() {
    return this.tasks;
  }

  public @Nullable NPC getCorpse() {
    return this.corpse;
  }

  public void setCorpse(final @Nullable NPC corpse) {
    this.corpse = corpse;
  }

  public List<ItemStack> getDeathLoot() {
    return this.deathLoot;
  }

  public void setDeathLoot(final List<ItemStack> deathLoot) {
    this.deathLoot.clear();
    this.deathLoot.addAll(deathLoot);
  }
}
