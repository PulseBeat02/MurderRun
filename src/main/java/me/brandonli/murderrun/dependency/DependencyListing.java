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
package me.brandonli.murderrun.dependency;

import java.util.*;
import me.brandonli.murderrun.utils.versioning.ServerEnvironment;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DependencyListing {

  private static final Map<String, DependencyListing> DEPENDENCY_LISTINGS = new HashMap<>();
  private static final Dependency CITIZENS = new UrlDependency(
    "Citizens",
    "Citizens-2.0.37-b3763",
    "https://ci.citizensnpcs.co/job/Citizens2/3763/artifact/dist/target/Citizens-2.0.37-b3763.jar"
  );

  private static final Dependency WORLD_EDIT = new UrlDependency(
    "WorldEdit",
    "worldedit-bukkit-7.3.12-beta-01",
    "https://cdn.modrinth.com/data/1u6JkXh5/versions/NhJaettg/worldedit-bukkit-7.3.12-beta-01.jar"
  );

  private static final Dependency PACKET_EVENTS = new UrlDependency(
    "PacketEvents",
    "packetevents-spigot-2.8.0-SNAPSHOT",
    "https://ci.codemc.io/job/retrooper/job/packetevents/668/artifact/spigot/build/libs/packetevents-spigot-2.8.0-SNAPSHOT.jar"
  );

  private static final DependencyListing V1_21_R3 = create("V1_21_R3", CITIZENS, WORLD_EDIT, PACKET_EVENTS);

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
