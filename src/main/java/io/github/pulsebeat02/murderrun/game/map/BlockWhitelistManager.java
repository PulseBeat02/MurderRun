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
package io.github.pulsebeat02.murderrun.game.map;

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
