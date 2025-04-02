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
package me.brandonli.murderrun.data.dfu;

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
