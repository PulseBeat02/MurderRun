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
package me.brandonli.murderrun.game;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;

public enum GameMode {
  DEFAULT("default", "Default"),
  ONE_BOUNCE("one_bounce", "One Bounce"),
  FREEZE_TAG("freeze_tag", "Freeze Tag");

  private static final Map<String, GameMode> KEY_LOOKUP = Map.of(
      DEFAULT.modeName, DEFAULT, ONE_BOUNCE.modeName, ONE_BOUNCE, FREEZE_TAG.modeName, FREEZE_TAG);

  private static Map<GameMode, GameProperties> PROPERTIES_LOOKUP;

  private final String modeName;
  private final String displayName;

  GameMode(final String modeName, final String displayName) {
    this.modeName = modeName;
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public String getModeName() {
    return this.modeName;
  }

  public GameProperties getProperties() {
    if (PROPERTIES_LOOKUP == null) { // lazily load the properties
      PROPERTIES_LOOKUP = Map.of(
          DEFAULT,
          new GameProperties(DEFAULT),
          ONE_BOUNCE,
          new GameProperties(ONE_BOUNCE),
          FREEZE_TAG,
          new GameProperties(FREEZE_TAG));
    }
    return requireNonNull(PROPERTIES_LOOKUP.get(this));
  }

  public static Optional<GameMode> fromString(final String modeName) {
    final String lower = modeName.toLowerCase();
    final GameMode mode = KEY_LOOKUP.get(lower);
    return Optional.ofNullable(mode);
  }
}
