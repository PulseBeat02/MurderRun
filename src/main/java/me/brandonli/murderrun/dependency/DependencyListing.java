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
package me.brandonli.murderrun.dependency;

import java.util.*;
import me.brandonli.murderrun.utils.versioning.ServerEnvironment;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DependencyListing {

  private static final Map<String, DependencyListing> DEPENDENCY_LISTINGS = new HashMap<>();
  private static final Dependency CITIZENS = new UrlDependency(
    "Citizens",
    "Citizens-2.0.38-b3801",
    "https://ci.citizensnpcs.co/job/citizens2/3801/artifact/dist/target/Citizens-2.0.38-b3801.jar"
  );

  private static final Dependency WORLD_EDIT = new UrlDependency(
    "WorldEdit",
    "worldedit-bukkit-7.3.13.jar",
    "https://cdn.modrinth.com/data/1u6JkXh5/versions/U0uDF7yg/worldedit-bukkit-7.3.13.jar"
  );

  private static final Dependency PACKET_EVENTS = new UrlDependency(
    "PacketEvents",
    "packetevents-spigot-2.8.1-SNAPSHOT",
    "https://ci.codemc.io/job/retrooper/job/packetevents/692/artifact/spigot/build/libs/packetevents-spigot-2.8.1-SNAPSHOT.jar"
  );

  private static final DependencyListing V1_21_R4 = create("V1_21_R4", CITIZENS, WORLD_EDIT, PACKET_EVENTS);

  private static DependencyListing create(final String revision, final Dependency... deps) {
    final Collection<Dependency> dependencies = List.of(deps);
    final DependencyListing listing = new DependencyListing(revision, dependencies);
    DEPENDENCY_LISTINGS.put(listing.revision, listing);
    return listing;
  }

  public static @Nullable DependencyListing getCurrentListing() {
    final String revision = ServerEnvironment.getNMSRevision();
    final String upper = revision.toUpperCase();
    return DEPENDENCY_LISTINGS.get(upper);
  }

  private final String revision;
  private final Collection<Dependency> dependencies;

  DependencyListing(final String revision, final Collection<Dependency> dependencies) {
    this.revision = revision;
    this.dependencies = dependencies;
  }

  public String getRevision() {
    return this.revision;
  }

  public Collection<Dependency> getDependencies() {
    return this.dependencies;
  }
}
