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
package io.github.pulsebeat02.murderrun.game.player;

import static net.kyori.adventure.bossbar.BossBar.bossBar;
import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.sound.SoundStop.named;
import static net.kyori.adventure.title.Title.title;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundResource;
import java.util.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class PlayerAudience {

  private final Audience audience;
  private final Map<String, BossBar> bars;

  public PlayerAudience(final Game game, final UUID uuid) {
    this.audience = this.getAudience(game, uuid);
    this.bars = new HashMap<>();
  }

  private Audience getAudience(@UnderInitialization PlayerAudience this, final Game game, final UUID uuid) {
    final MurderRun plugin = game.getPlugin();
    final AudienceProvider handler = plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    return audiences.player(uuid);
  }

  public void playSound(final SoundResource key) {
    this.playSound(key.getKey());
  }

  public void playSound(final Key key) {
    this.playSound(key, Source.MASTER, 1.0f, 1.0f);
  }

  public void playSound(final String key) {
    this.playSound(key(key));
  }

  public void sendMessage(final Component component) {
    this.audience.sendMessage(component);
  }

  public void showTitle(final Component title, final Component subtitle) {
    this.audience.showTitle(title(title, subtitle));
  }

  public void showBossBar(
    final String id,
    final Component name,
    final float progress,
    final BossBar.Color color,
    final BossBar.Overlay overlay
  ) {
    final BossBar bar = bossBar(name, progress, color, overlay);
    this.bars.put(id, bar);
    this.audience.showBossBar(bar);
  }

  public void updateBossBar(final String id, final float progress) {
    final BossBar bar = this.bars.get(id);
    if (bar != null) {
      bar.progress(progress);
    }
  }

  public void stopSound(final SoundResource key) {
    this.stopSound(key.getKey());
  }

  public void stopSound(final Key key) {
    this.audience.stopSound(named(key));
  }

  public void playSound(final Key key, final Source category, final float volume, final float pitch) {
    this.audience.playSound(sound(key, category, volume, pitch));
  }

  public void removeAllBossBars() {
    final Collection<BossBar> bars = this.bars.values();
    for (final BossBar bar : bars) {
      this.audience.hideBossBar(bar);
    }
  }
}
