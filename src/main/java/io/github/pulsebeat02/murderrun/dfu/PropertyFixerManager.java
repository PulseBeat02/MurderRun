/*

MIT License

Copyright (c) 2025 Brandon Li

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
package io.github.pulsebeat02.murderrun.dfu;

import java.util.*;
import java.util.function.Consumer;

public final class PropertyFixerManager {

  private static final Consumer<ResourceBundle> DEFAULT_FIXER = properties -> {};

  private final Map<PropertyVersion, Consumer<ResourceBundle>> fixers;
  private final Set<PropertyVersion> versionOrder;

  public PropertyFixerManager() {
    this.fixers = new EnumMap<>(PropertyVersion.class);
    this.versionOrder = new TreeSet<>(PropertyVersion::compareTo);
  }

  public void registerGamePropertiesFixer() {
    this.registerFixer(PropertyVersion.v1_0_0, DEFAULT_FIXER);
  }

  public void registerLocalePropertiesFixer() {
    this.registerFixer(PropertyVersion.v1_0_0, DEFAULT_FIXER);
  }

  private void registerFixer(final PropertyVersion version, final Consumer<ResourceBundle> fixer) {
    this.fixers.put(version, fixer);
    this.versionOrder.add(version);
  }

  public void applyFixersUpTo(final ResourceBundle properties) {
    final PropertyVersion currentVersion = PropertyVersion.getVersion(properties);
    for (final PropertyVersion version : this.versionOrder) {
      if (version.compareTo(currentVersion) > 0) {
        break;
      }
      final Consumer<ResourceBundle> fixer = this.fixers.get(version);
      if (fixer != null) {
        fixer.accept(properties);
      }
    }
  }
}
