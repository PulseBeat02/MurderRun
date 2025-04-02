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
package me.brandonli.murderrun.game.extension.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.event.platform.PlatformUnreadyEvent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.eventbus.EventBus;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import me.brandonli.murderrun.MurderRun;

public final class WESpreader {

  private final MurderRun plugin;

  public WESpreader(final MurderRun plugin) {
    this.plugin = plugin;
  }

  public void load() {
    final WorldEdit worldEdit = WorldEdit.getInstance();
    final EventBus eventBus = worldEdit.getEventBus();
    eventBus.register(this);
  }

  @Subscribe(priority = EventHandler.Priority.VERY_EARLY)
  public void onPlatformUnready(final PlatformUnreadyEvent event) {
    final WorldEdit worldEdit = WorldEdit.getInstance();
    final EventBus eventBus = worldEdit.getEventBus();
    eventBus.unregister(this);
  }

  @Subscribe
  public void onEditStage(final EditSessionEvent event) {
    final EditSession.Stage stage = event.getStage();
    if (stage == EditSession.Stage.BEFORE_CHANGE) {
      final Extent extent = event.getExtent();
      final SchedulingExtent schedulingExtent = new SchedulingExtent(extent, this.plugin);
      event.setExtent(schedulingExtent);
    }
  }
}
