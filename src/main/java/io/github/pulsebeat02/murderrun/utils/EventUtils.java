package io.github.pulsebeat02.murderrun.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class EventUtils {

  private EventUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static @Nullable Location getProjectileLocation(final ProjectileHitEvent event) {
    final Block block = event.getHitBlock();
    final Entity hitEntity = event.getHitEntity();
    return block != null ? block.getLocation() : (hitEntity != null ? hitEntity.getLocation() : null);
  }
}
