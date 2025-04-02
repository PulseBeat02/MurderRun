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
package io.github.pulsebeat02.murderrun.reflect.versioning;

import static java.util.Objects.requireNonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public final class ServerEnvironment {

  private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?");
  private static final int[] DEFAULT_REVISION = new int[] { 1, 8, 8 };
  private static final String MINECRAFT_PACKAGE = getMinecraftPackage();

  public static String getMinecraftPackage() {
    final int[] version = getVersion();
    final String versionString = "v%d_%d_R".formatted(version[0], version[1]);
    final String versionChecker = getVersionPackage();
    String revision = null;
    for (int i = 1; i <= 10; i++) {
      final String rev = versionString + i;
      final String check = versionChecker.formatted(rev);
      if (checkClassExists(check)) {
        revision = rev;
        break;
      }
    }
    if (revision == null) {
      throw new AssertionError("Unable to find server revision!");
    }
    return revision;
  }

  private static @NotNull String getVersionPackage() {
    String versionChecker = "org.bukkit.craftbukkit.%s.CraftServer";
    final Server server = Bukkit.getServer();
    final Class<?> clazz = server.getClass();
    final String name = clazz.getName();
    if (name.equals("org.bukkit.craftbukkit.CraftServer")) {
      versionChecker = "net.citizensnpcs.nms.%s.util.NMSImpl";
    }
    return versionChecker;
  }

  private static boolean checkClassExists(final String className) {
    try {
      Class.forName(className);
      return true;
    } catch (final ClassNotFoundException e) {
      return false;
    }
  }

  private static int[] getVersion() {
    final String version = Bukkit.getBukkitVersion();
    if (version.isEmpty()) {
      return DEFAULT_REVISION;
    }
    final Matcher matcher = VERSION_PATTERN.matcher(version);
    if (matcher.find()) {
      final String majorGroup = requireNonNull(matcher.group(1));
      final String minorGroup = requireNonNull(matcher.group(2));
      final String patchGroup = matcher.group(3);
      final int major = Integer.parseInt(majorGroup);
      final int minor = Integer.parseInt(minorGroup);
      final int patch = patchGroup != null ? Integer.parseInt(patchGroup) : 0;
      return new int[] { major, minor, patch };
    }
    return DEFAULT_REVISION;
  }

  public static String getNMSRevision() {
    return MINECRAFT_PACKAGE;
  }
}
