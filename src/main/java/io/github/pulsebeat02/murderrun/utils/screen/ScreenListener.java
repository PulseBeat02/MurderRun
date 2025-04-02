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
package io.github.pulsebeat02.murderrun.utils.screen;

import java.util.Objects;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class ScreenListener implements Listener {

  private final Set<Player> tracked;

  public ScreenListener(final Set<Player> tracked) {
    this.tracked = tracked;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onCreatureSpawnEvent(final CreatureSpawnEvent event) {
    final Location eventLocation = event.getLocation();
    for (final Player player : this.tracked) {
      final Location playerLocation = player.getLocation();
      if (!this.equalLocations(eventLocation, playerLocation)) {
        continue;
      }
      event.setCancelled(true);
      return;
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerTeleportEvent(final PlayerTeleportEvent event) {
    final Location eventLocation = event.getTo();
    if (eventLocation == null) {
      return;
    }
    for (final Player player : this.tracked) {
      final Location playerLocation = player.getLocation();
      if (!this.equalLocations(eventLocation, playerLocation)) {
        continue;
      }
      event.setCancelled(true);
      return;
    }
  }

  private boolean equalLocations(final Location loc1, final Location loc2) {
    return (
      Objects.equals(loc1.getWorld(), loc2.getWorld()) &&
      loc1.getX() == loc2.getX() &&
      loc1.getY() == loc2.getY() &&
      loc1.getZ() == loc2.getZ()
    );
  }
}
