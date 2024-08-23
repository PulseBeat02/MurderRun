package io.github.pulsebeat02.murderrun.resourcepack.sound;

import java.util.HashSet;
import java.util.Set;

public final class Sounds {

  private static final Set<SoundResource> ALL = new HashSet<>();

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

  private static SoundResource of(final String name) {
    final SoundFile sound = new SoundFile(name);
    final SoundResource soundResource = new SoundResource(sound);
    ALL.add(soundResource);
    return soundResource;
  }

  public static Set<SoundResource> getAllSounds() {
    return ALL;
  }
}
