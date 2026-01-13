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
package me.brandonli.murderrun.resourcepack.sound;

import static net.kyori.adventure.key.Key.key;

import java.util.HashSet;
import java.util.Set;
import me.brandonli.murderrun.utils.immutable.Keys;
import net.kyori.adventure.key.Key;

public final class Sounds {

  private static final Set<SoundResource> ALL = new HashSet<>();

  private Sounds() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static final SoundResource COUNTDOWN = of("countdown");
  public static final SoundResource CHAINSAW = of("chainsaw");
  public static final SoundResource DEATH = of("death");
  public static final SoundResource RELEASED_1 = of("released_1");
  public static final SoundResource RELEASED_2 = of("released_2");
  public static final SoundResource LOSS = of("loss");
  public static final SoundResource WIN = of("win");
  public static final SoundResource JUMP_SCARE = of("jump_scare");
  public static final SoundResource FART = of("fart");
  public static final SoundResource SUPPLY_DROP = of("supply_drop");
  public static final SoundResource FLASHBANG = of("flashbang");
  public static final SoundResource FLASHLIGHT = of("flashlight");
  public static final SoundResource BACKGROUND = of("background");
  public static final SoundResource REWIND = of("rewind");
  public static final SoundResource PORTAL = of("portal");
  public static final SoundResource HEARTBEAT = of("heartbeat");
  public static final SoundResource AMBIENCE = of("ambience");

  private static SoundResource of(final String name) {
    final Key key = key(Keys.NAMESPACE, name);
    final SoundResource soundResource = new SoundResource(key);
    ALL.add(soundResource);
    return soundResource;
  }

  public static Set<SoundResource> getAllSounds() {
    return ALL;
  }
}
