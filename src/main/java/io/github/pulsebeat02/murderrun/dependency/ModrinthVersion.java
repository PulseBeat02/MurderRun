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
package io.github.pulsebeat02.murderrun.dependency;

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
