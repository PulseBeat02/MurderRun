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

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public final class ServerEnvironment {

  private static final String PATTERN = "(?i)\\(MC: (\\d)\\.(\\d++)\\.?(\\d++)?(?: (Pre-Release|Release Candidate) )?(\\d)?\\)";
  private static final Pattern VERSION_PATTERN = Pattern.compile(PATTERN);
  private static final String PACKAGE_PATTERN = "v1_%s_R%s";

  private static final String NMS_REVISION;

  static {
    final String raw = Bukkit.getVersion();
    final Matcher matcher = VERSION_PATTERN.matcher(raw);
    if (matcher.find()) {
      final MatchResult matchResult = matcher.toMatchResult();
      final String version = matchResult.group(2);
      final String patchVersion = matchResult.group(3);
      NMS_REVISION = PACKAGE_PATTERN.formatted(version, patchVersion);
    } else {
      throw new UnsupportedOperationException("The current server version is not supported!");
    }
  }

  public static String getNMSRevision() {
    return NMS_REVISION;
  }
}
