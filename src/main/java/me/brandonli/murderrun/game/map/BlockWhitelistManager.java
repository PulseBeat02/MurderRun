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
package me.brandonli.murderrun.game.map;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.history.change.BlockChange;
import com.sk89q.worldedit.history.change.Change;
import com.sk89q.worldedit.history.changeset.ChangeSet;
import com.sk89q.worldedit.math.BlockVector3;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.block.Block;

public final class BlockWhitelistManager {

  private final Set<BlockVector3> whitelisted;

  public BlockWhitelistManager() {
    this.whitelisted = new HashSet<>();
  }

  public boolean checkAndRemoveBlock(final Block block) {
    final Location location = block.getLocation();
    final BlockVector3 vector = BukkitAdapter.asBlockVector(location);
    if (this.isBlockWhitelisted(block)) {
      this.whitelisted.remove(vector);
      return true;
    }
    return false;
  }

  public boolean isBlockWhitelisted(final Block block) {
    final Location location = block.getLocation();
    final BlockVector3 vector = BukkitAdapter.asBlockVector(location);
    return this.whitelisted.contains(vector);
  }

  public void addWhitelistedBlocks(final EditSession session) {
    final ChangeSet set = session.getChangeSet();
    final Iterator<Change> iterator = set.forwardIterator();
    while (iterator.hasNext()) {
      final Change change = iterator.next();
      this.addVector(change);
    }
  }

  public void addWhitelistedBlock(final Block block) {
    final Location location = block.getLocation();
    final BlockVector3 vector = BukkitAdapter.asBlockVector(location);
    this.whitelisted.add(vector);
  }

  private void addVector(final Change change) {
    if (change instanceof final BlockChange blockChange) {
      final BlockVector3 vector = blockChange.position();
      this.whitelisted.add(vector);
    }
  }
}
