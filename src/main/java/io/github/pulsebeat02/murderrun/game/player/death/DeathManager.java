/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.player.death;

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
