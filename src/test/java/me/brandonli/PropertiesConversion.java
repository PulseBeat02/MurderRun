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
package me.brandonli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public final class PropertiesConversion {

  public static void main(final String[] args) throws IOException {
    final String dir = System.getProperty("user.dir");
    final Path path =
        Path.of(dir, "src/main/java", "me/brandonli/murderrun", "game/GameProperties.java");
    final List<String> lines = Files.readAllLines(path);
    final int start = 32;
    final int end = 504;
    for (int i = start; i <= end; i++) {
      final String line = lines.get(i);
      final String trimmed = line.trim();
      final String[] tokens = trimmed.split(" ");
      final String type = tokens[0];
      final String name = tokens[1];
      final String value = tokens[3];
      final String methodName = constantToGetter(name);
      final String method = "public %s %s() { return %s }".formatted(type, methodName, value);
      System.out.println(method);
    }
  }

  private static String constantToGetter(final String constantName) {
    final String clean = constantName.replaceAll("[^A-Za-z0-9_]", "");
    final String[] parts = clean.toLowerCase(Locale.ROOT).split("_");
    final StringBuilder sb = new StringBuilder("get");
    for (final String p : parts) {
      if (p.isEmpty()) {
        continue;
      }
      sb.append(Character.toUpperCase(p.charAt(0)));
      if (p.length() > 1) {
        sb.append(p.substring(1));
      }
    }
    return sb.toString();
  }
}
