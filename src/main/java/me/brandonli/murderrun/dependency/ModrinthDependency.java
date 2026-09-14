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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ModrinthDependency implements Dependency {

  private static final String VERSIONS_URL =
      "https://api.modrinth.com/v2/project/%s/version?game_versions=%s";
  private static final Set<String> SUPPORTED_LOADERS =
      Set.of("paper", "bukkit", "spigot", "folia", "purpur");
  private static final Comparator<ModrinthVersion> PREFERENCE = Comparator.comparingInt(
          ModrinthVersion::getReleasePriority)
      .thenComparing(ModrinthVersion::getPublished);
  private static final Gson GSON = new Gson();

  private final String name;
  private final String project;

  public ModrinthDependency(final String name, final String project) {
    this.name = name;
    this.project = project;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public DependencyArtifact resolve(final DependencyClient client, final String minecraftVersion) {
    final URI uri = this.createVersionsUri(minecraftVersion);
    final String json = client.getText(uri);
    return this.parse(json, minecraftVersion);
  }

  DependencyArtifact parse(final String json, final String minecraftVersion) {
    final ModrinthVersion[] versions = this.deserialize(json);
    final Optional<ModrinthVersion> best = Arrays.stream(versions)
        .filter(version -> version.isCompatible(minecraftVersion, SUPPORTED_LOADERS))
        .max(PREFERENCE);
    if (best.isEmpty()) {
      final String message =
          "No %s build on Modrinth supports Minecraft %s".formatted(this.name, minecraftVersion);
      throw new DependencyException(message);
    }
    final ModrinthVersion version = best.get();
    final Optional<ModrinthFile> jar = version.findJar();
    final ModrinthFile file = jar.orElseThrow();
    final String url = file.getUrl();
    final String fileName = file.getFilename();
    if (url == null || fileName == null) {
      final String message = "Modrinth returned an incomplete file for %s".formatted(this.name);
      throw new DependencyException(message);
    }
    final URI uri = this.createUri(url);
    final @Nullable String sha512 = file.getSha512();
    return new DependencyArtifact(fileName, uri, sha512);
  }

  private ModrinthVersion[] deserialize(final String json) {
    try {
      final ModrinthVersion[] versions = GSON.fromJson(json, ModrinthVersion[].class);
      return versions == null ? new ModrinthVersion[0] : versions;
    } catch (final JsonSyntaxException e) {
      final String message = "Modrinth returned invalid JSON for %s".formatted(this.name);
      throw new DependencyException(message, e);
    }
  }

  private URI createVersionsUri(final String minecraftVersion) {
    final String filter = "[\"%s\"]".formatted(minecraftVersion);
    final String encoded = URLEncoder.encode(filter, StandardCharsets.UTF_8);
    final String url = VERSIONS_URL.formatted(this.project, encoded);
    return this.createUri(url);
  }

  private URI createUri(final String url) {
    try {
      return URI.create(url);
    } catch (final IllegalArgumentException e) {
      final String message = "Invalid URL %s for %s".formatted(url, this.name);
      throw new DependencyException(message, e);
    }
  }
}
