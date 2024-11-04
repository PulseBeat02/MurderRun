/*

MIT License

Copyright (c) 2024 Brandon Li

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
package io.github.pulsebeat02.murderrun.resourcepack.sound;

import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.immutable.Keys;
import io.github.pulsebeat02.murderrun.utils.IOUtils;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.sound.Sound;

public final class SoundFile {

  private static final String PLUGIN_SOUND_RESOURCE = "sounds/%s.ogg";

  private final Key key;
  private final String name;
  private final String path;

  public SoundFile(final String name) {
    this.name = name;
    this.key = key(Keys.NAMESPACE, this.name);
    this.path = PLUGIN_SOUND_RESOURCE.formatted(name);
  }

  public Sound stitchSound() {
    final Writable writable = IOUtils.getWritableStreamFromResource(this.path);
    return Sound.sound(this.key, writable);
  }

  public Key getKey() {
    return this.key;
  }

  public String getName() {
    return this.name;
  }

  public String getPath() {
    return this.path;
  }
}
