package io.github.pulsebeat02.murderrun.game.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.event.platform.PlatformUnreadyEvent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.eventbus.EventBus;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import io.github.pulsebeat02.murderrun.MurderRun;

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
