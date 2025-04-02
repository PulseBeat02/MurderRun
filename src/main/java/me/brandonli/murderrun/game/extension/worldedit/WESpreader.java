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
