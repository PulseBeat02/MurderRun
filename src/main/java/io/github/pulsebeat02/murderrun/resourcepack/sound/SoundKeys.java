package io.github.pulsebeat02.murderrun.resourcepack.sound;

import org.checkerframework.checker.initialization.qual.UnderInitialization;
import team.unnamed.creative.sound.Sound;

public enum SoundKeys {
  COUNTDOWN("countdown"),
  CHAINSAW("chainsaw"),
  DEATH("death"),
  RELEASED_1("released_1"),
  RELEASED_2("released_2"),
  LOSS("loss"),
  WIN("win"),
  JUMP_SCARE("jump_scare"),
  FART("fart");

  private static final String PATH_RESOURCE = "murder_run:%s";

  private final Sound sound;
  private final String id;
  private final String namespace;

  SoundKeys(final String id) {
    this.sound = this.loadSound(id);
    this.id = id;
    this.namespace = String.format(PATH_RESOURCE, id);
  }

  private Sound loadSound(@UnderInitialization SoundKeys this, final String name) {
    final ResourcePackSound ogg = new ResourcePackSound(name);
    return ogg.build();
  }

  public String getSoundName() {
    return this.namespace;
  }

  public Sound getSound() {
    return this.sound;
  }

  public String getId() {
    return this.id;
  }
}
