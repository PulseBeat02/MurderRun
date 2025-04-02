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
package me.brandonli.murderrun.utils.immutable;

public final class Holder<T> {

  private static final Holder<?> EMPTY = new Holder<>(null);

  private final T value;

  private Holder(final T value) {
    this.value = value;
  }

  public static <T> Holder<T> of(final T value) {
    return new Holder<>(value);
  }

  @SuppressWarnings("unchecked")
  public static <T> Holder<T> empty() {
    return (Holder<T>) EMPTY;
  }

  public T get() {
    return this.value;
  }

  public boolean isPresent() {
    return this.value != null;
  }

  public boolean isEmpty() {
    return this.value == null;
  }
}
