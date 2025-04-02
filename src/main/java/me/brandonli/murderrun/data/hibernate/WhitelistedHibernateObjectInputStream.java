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
package me.brandonli.murderrun.data.hibernate;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Set;

public final class WhitelistedHibernateObjectInputStream extends ObjectInputStream {

  private static final Set<String> WHITELISTED_CLASSES = Set.of("[Ljava.lang.Long;");

  public WhitelistedHibernateObjectInputStream(final InputStream in) throws IOException {
    super(in);
  }

  @Override
  protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
    final String name = desc.getName();
    if (!WHITELISTED_CLASSES.contains(name)) {
      final String msg = "Unauthorized deserialization attempt for class: %s".formatted(name);
      throw new AssertionError(msg);
    }
    return super.resolveClass(desc);
  }
}
