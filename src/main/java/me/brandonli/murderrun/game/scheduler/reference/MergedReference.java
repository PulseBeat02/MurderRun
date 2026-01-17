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
package me.brandonli.murderrun.game.scheduler.reference;

import java.util.List;

public final class MergedReference<U, V> extends SchedulerReference<List<Reference<?>>> {

  MergedReference(final Reference<U> first, final Reference<V> second) {
    super(List.of(first, second));
  }

  public static <A, B> MergedReference<A, B> of(
      final Reference<A> first, final Reference<B> second) {
    return new MergedReference<>(first, second);
  }

  @Override
  public boolean isInvalid() {
    final List<Reference<?>> references = this.get();
    final Reference<?> first = references.getFirst();
    final Reference<?> second = references.getLast();
    return first.isInvalid() || second.isInvalid();
  }
}
