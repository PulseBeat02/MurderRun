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
package io.github.pulsebeat02.murderrun.utils.versioning;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import org.bukkit.Bukkit;

public final class ServerEnvironment {

  private ServerEnvironment() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  private static final Map<ServerVersion, String> VERSION_MAP = Map.of(
    ServerVersion.V_1_21_4,
    "v1_21_R3",
    ServerVersion.V_1_21_5,
    "v1_21_R4"
  );
  private static final String MINECRAFT_PACKAGE;

  static {
    final ServerVersion version = getVersion();
    MINECRAFT_PACKAGE = requireNonNull(VERSION_MAP.get(version));
  }

  private static ServerVersion getVersion() {
    final String bukkitVersion = Bukkit.getBukkitVersion();
    final ServerVersion fallbackVersion = ServerVersion.V_1_8_8;
    if (bukkitVersion.contains("Unknown")) {
      return fallbackVersion;
    } else {
      final ServerVersion[] reversed = ServerVersion.getReversed();
      for (final ServerVersion val : reversed) {
        final String name = val.getReleaseName();
        if (bukkitVersion.contains(name)) {
          return val;
        }
      }
    }
    return fallbackVersion;
  }

  public static String getNMSRevision() {
    return MINECRAFT_PACKAGE;
  }
}
