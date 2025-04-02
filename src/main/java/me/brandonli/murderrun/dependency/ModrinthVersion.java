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
import java.util.Arrays;
import java.util.Optional;

public class ModrinthVersion {

  private final String name;
  private final String version_number;
  private final String version_type;
  private final String status;
  private final String requested_status;
  private final String id;
  private final String[] game_versions;
  private final ModrinthFile[] files;

  public ModrinthVersion(
    final String name,
    final String version_number,
    final String version_type,
    final String status,
    final String requested_status,
    final String id,
    final String[] game_versions,
    final ModrinthFile[] files
  ) {
    this.name = name;
    this.version_number = version_number;
    this.version_type = version_type;
    this.status = status;
    this.requested_status = requested_status;
    this.id = id;
    this.files = files;
    this.game_versions = game_versions;
  }

  public static ModrinthVersion[] serializeVersions(final String json) {
    final Gson gson = new Gson(); // since using the default one will use worldedit class serializer
    final ModrinthVersion[] versions = gson.fromJson(json, ModrinthVersion[].class);
    return Arrays.stream(versions).filter(ModrinthVersion::isValidVersion).toArray(ModrinthVersion[]::new);
  }

  public Optional<ModrinthFile> findFirstValidFile() {
    return Arrays.stream(this.files).filter(ModrinthFile::isValidFile).findFirst();
  }

  public boolean isValidVersion() {
    return this.files != null && this.isListedProject();
  }

  public boolean isListedProject() {
    return this.status.equals("listed");
  }

  public String getName() {
    return this.name;
  }

  public String getVersionNumber() {
    return this.version_number;
  }

  public String getVersionType() {
    return this.version_type;
  }

  public String getStatus() {
    return this.status;
  }

  public String getRequestedStatus() {
    return this.requested_status;
  }

  public String getId() {
    return this.id;
  }

  public ModrinthFile[] getFiles() {
    return this.files;
  }

  public String[] getGame_versions() {
    return this.game_versions;
  }
}
