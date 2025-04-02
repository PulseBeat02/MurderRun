/*

MIT License

Copyright (c) 2025 Brandon Li

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
package io.github.pulsebeat02.murderrun.game.gadget.killer.utility.tool;

import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Portal {

  private @Nullable Location location;
  private final Collection<BukkitTask> tasks;

  public Portal(final @Nullable Location location) {
    this.location = location;
    this.tasks = new HashSet<>();
  }

  public boolean isInvalidPortal() {
    return this.location == null;
  }

  public boolean isValidPortal() {
    return this.location != null;
  }

  public @Nullable Location getLocation() {
    return this.location;
  }

  public void setLocation(final @Nullable Location location) {
    this.location = location;
  }

  public Collection<BukkitTask> getTasks() {
    return this.tasks;
  }

  public void addTask(final BukkitTask task) {
    this.tasks.add(task);
  }
}
