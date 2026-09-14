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

import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ModrinthVersion {

  private static final String LISTED = "listed";

  @SerializedName("version_number")
  private final @Nullable String versionNumber;

  @SerializedName("version_type")
  private final @Nullable String versionType;

  private final @Nullable String status;

  @SerializedName("date_published")
  private final @Nullable String datePublished;

  @SerializedName("game_versions")
  private final @Nullable List<String> gameVersions;

  private final @Nullable List<String> loaders;
  private final @Nullable List<ModrinthFile> files;

  public ModrinthVersion(
      final @Nullable String versionNumber,
      final @Nullable String versionType,
      final @Nullable String status,
      final @Nullable String datePublished,
      final @Nullable List<String> gameVersions,
      final @Nullable List<String> loaders,
      final @Nullable List<ModrinthFile> files) {
    this.versionNumber = versionNumber;
    this.versionType = versionType;
    this.status = status;
    this.datePublished = datePublished;
    this.gameVersions = gameVersions;
    this.loaders = loaders;
    this.files = files;
  }

  public boolean isCompatible(
      final String minecraftVersion, final Collection<String> supportedLoaders) {
    return this.isListed()
        && this.supportsGameVersion(minecraftVersion)
        && this.supportsAnyLoader(supportedLoaders)
        && this.findJar().isPresent();
  }

  public boolean isListed() {
    return LISTED.equals(this.status);
  }

  public boolean supportsGameVersion(final String minecraftVersion) {
    return this.gameVersions != null && this.gameVersions.contains(minecraftVersion);
  }

  public boolean supportsAnyLoader(final Collection<String> supportedLoaders) {
    return this.loaders != null && this.loaders.stream().anyMatch(supportedLoaders::contains);
  }

  public Optional<ModrinthFile> findJar() {
    if (this.files == null) {
      return Optional.empty();
    }
    final Optional<ModrinthFile> primary = this.files.stream()
        .filter(ModrinthFile::isJar)
        .filter(ModrinthFile::isPrimary)
        .findFirst();
    if (primary.isPresent()) {
      return primary;
    }
    return this.files.stream().filter(ModrinthFile::isJar).findFirst();
  }

  public int getReleasePriority() {
    if (this.versionType == null) {
      return 0;
    }
    return switch (this.versionType) {
      case "release" -> 2;
      case "beta" -> 1;
      default -> 0;
    };
  }

  public Instant getPublished() {
    if (this.datePublished == null) {
      return Instant.EPOCH;
    }
    try {
      return Instant.parse(this.datePublished);
    } catch (final DateTimeParseException e) {
      return Instant.EPOCH;
    }
  }

  public @Nullable String getVersionNumber() {
    return this.versionNumber;
  }

  public @Nullable String getVersionType() {
    return this.versionType;
  }

  public @Nullable String getStatus() {
    return this.status;
  }

  public @Nullable String getDatePublished() {
    return this.datePublished;
  }

  public @Nullable List<String> getGameVersions() {
    return this.gameVersions;
  }

  public @Nullable List<String> getLoaders() {
    return this.loaders;
  }

  public @Nullable List<ModrinthFile> getFiles() {
    return this.files;
  }
}
