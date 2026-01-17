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

import be.seeseemelk.mockbukkit.MockBukkit;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.util.List;
import me.brandonli.murderrun.game.gadget.Gadget;

public final class TrapExampleTest {

  public static void main(final String[] args) {
    MockBukkit.mock();
    try (final ScanResult scanResult = new ClassGraph().enableClassInfo().scan()) {
      final List<ClassInfo> traps =
          scanResult.getClassesImplementing(Gadget.class).filter(info -> !info.isAbstract());
      traps.forEach(trap -> {
        final String spaced = trap.getSimpleName().replaceAll("(?=[A-Z])", " ");
        System.out.println(spaced.substring(1));
      });
    }
  }
}
