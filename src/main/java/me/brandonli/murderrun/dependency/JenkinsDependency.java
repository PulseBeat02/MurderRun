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
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class JenkinsDependency implements Dependency {

  private static final String API_PATH =
      "lastSuccessfulBuild/api/json?tree=number,artifacts[relativePath]";
  private static final String BUILD_NUMBER_PATH = "lastSuccessfulBuild/buildNumber";
  private static final Pattern PAGE_ARTIFACT = Pattern.compile("artifact/([^\"'<>\\s]+)");
  private static final Gson GSON = new Gson();

  private final String name;
  private final String job;
  private final Pattern artifact;

  public JenkinsDependency(final String name, final String job, final String artifactRegex) {
    this.name = name;
    this.job = job.endsWith("/") ? job : job + "/";
    this.artifact = Pattern.compile(artifactRegex);
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public DependencyArtifact resolve(final DependencyClient client, final String minecraftVersion) {
    final Optional<DependencyArtifact> api = this.resolveFromApi(client);
    if (api.isPresent()) {
      return api.get();
    }
    final int number = this.findBuildNumber(client);
    final String relativePath = this.findArtifactFromPage(client, number);
    return this.createArtifact(number, relativePath);
  }

  private Optional<DependencyArtifact> resolveFromApi(final DependencyClient client) {
    final String url = this.job + API_PATH;
    final URI uri = this.createUri(url);
    final String json;
    try {
      json = client.getText(uri);
    } catch (final DependencyException e) {
      return Optional.empty();
    }
    final Optional<JenkinsBuild> build = this.parseApi(json);
    if (build.isEmpty()) {
      return Optional.empty();
    }
    final JenkinsBuild found = build.get();
    final @Nullable Integer number = found.getNumber();
    final Optional<String> relativePath = this.findMatchingArtifact(found);
    if (number == null || relativePath.isEmpty()) {
      return Optional.empty();
    }
    final String path = relativePath.get();
    final DependencyArtifact artifact = this.createArtifact(number, path);
    return Optional.of(artifact);
  }

  Optional<JenkinsBuild> parseApi(final String json) {
    try {
      final @Nullable JenkinsBuild build = GSON.fromJson(json, JenkinsBuild.class);
      return Optional.ofNullable(build);
    } catch (final JsonSyntaxException e) {
      return Optional.empty();
    }
  }

  Optional<String> findMatchingArtifact(final JenkinsBuild build) {
    final @Nullable List<JenkinsArtifact> artifacts = build.getArtifacts();
    if (artifacts == null) {
      return Optional.empty();
    }
    for (final JenkinsArtifact artifact : artifacts) {
      final @Nullable String relativePath = artifact.getRelativePath();
      if (relativePath != null && this.matchesArtifact(relativePath)) {
        return Optional.of(relativePath);
      }
    }
    return Optional.empty();
  }

  private int findBuildNumber(final DependencyClient client) {
    final String url = this.job + BUILD_NUMBER_PATH;
    final URI uri = this.createUri(url);
    final String text = client.getText(uri);
    final String trimmed = text.trim();
    try {
      return Integer.parseInt(trimmed);
    } catch (final NumberFormatException e) {
      final String message =
          "Jenkins returned an invalid build number %s for %s".formatted(trimmed, this.name);
      throw new DependencyException(message, e);
    }
  }

  private String findArtifactFromPage(final DependencyClient client, final int number) {
    final String url = "%s%d/".formatted(this.job, number);
    final URI uri = this.createUri(url);
    final String html = client.getText(uri);
    return this.parsePage(html, url);
  }

  String parsePage(final String html, final String url) {
    final Matcher matcher = PAGE_ARTIFACT.matcher(html);
    while (matcher.find()) {
      final @Nullable String relativePath = matcher.group(1);
      if (relativePath != null && this.matchesArtifact(relativePath)) {
        return relativePath;
      }
    }
    final String pattern = this.artifact.pattern();
    final String message =
        "No artifact matching %s found for %s at %s".formatted(pattern, this.name, url);
    throw new DependencyException(message);
  }

  private DependencyArtifact createArtifact(final int number, final String relativePath) {
    final String fileName = this.getFileName(relativePath);
    final String url = "%s%d/artifact/%s".formatted(this.job, number, relativePath);
    final URI uri = this.createUri(url);
    return new DependencyArtifact(fileName, uri, null);
  }

  private URI createUri(final String url) {
    try {
      return URI.create(url);
    } catch (final IllegalArgumentException e) {
      final String message = "Invalid URL %s for %s".formatted(url, this.name);
      throw new DependencyException(message, e);
    }
  }

  private boolean matchesArtifact(final String relativePath) {
    final String fileName = this.getFileName(relativePath);
    final Matcher matcher = this.artifact.matcher(fileName);
    return matcher.matches();
  }

  private String getFileName(final String relativePath) {
    final int index = relativePath.lastIndexOf('/');
    return index < 0 ? relativePath : relativePath.substring(index + 1);
  }
}
