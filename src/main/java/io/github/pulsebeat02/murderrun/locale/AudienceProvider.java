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
package io.github.pulsebeat02.murderrun.locale;

import io.github.pulsebeat02.murderrun.MurderRun;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;

public final class AudienceProvider {

  private final BukkitAudiences audience;

  public AudienceProvider(final MurderRun neon) {
    this.audience = BukkitAudiences.create(neon);
  }

  public void shutdown() {
    if (this.audience != null) {
      this.audience.close();
    }
  }

  public BukkitAudiences retrieve() {
    this.checkStatus();
    return this.audience;
  }

  private void checkStatus() {
    if (this.audience == null) {
      throw new AssertionError("Tried to access Adventure when the plugin was disabled!");
    }
  }

  public void console(final Component component) {
    this.checkStatus();
    final Audience console = this.audience.console();
    console.sendMessage(component);
  }
}
