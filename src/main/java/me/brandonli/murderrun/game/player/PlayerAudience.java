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
package me.brandonli.murderrun.game.player;

import static net.kyori.adventure.bossbar.BossBar.bossBar;
import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.sound.SoundStop.named;
import static net.kyori.adventure.title.Title.title;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.resourcepack.sound.SoundResource;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PlayerAudience {

  private static final String DEFAULT_LAYER_ID = "default";

  private final Audience audience;
  private final Map<String, BossBar> bars;
  private final LayeredActionBar actionBars;
  private final LayeredTitles titles;

  public PlayerAudience(final Game game, final UUID uuid) {
    this.audience = this.getAudience(game, uuid);
    this.bars = new HashMap<>();
    this.actionBars = new LayeredActionBar();
    this.titles = new LayeredTitles();
  }

  private Audience getAudience(
      @UnderInitialization PlayerAudience this, final Game game, final UUID uuid) {
    final MurderRun plugin = game.getPlugin();
    final AudienceProvider handler = plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    return audiences.player(uuid);
  }

  public void stopSound(final SoundStop stop) {
    this.audience.stopSound(stop);
  }

  public void playSound(final SoundResource key) {
    this.playSound(key.getKey());
  }

  public void playSound(final SoundResource key, final float volume) {
    this.playSound(key.getKey(), Source.MASTER, volume, 1.0f);
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

  public void stopSound(final SoundResource key) {
    this.stopSound(key.getKey());
  }

  public void stopSound(final Key key) {
    this.audience.stopSound(named(key));
  }

  public void playSound(
      final Key key, final Source category, final float volume, final float pitch) {
    this.audience.playSound(sound(key, category, volume, pitch));
  }

  public void setActionBar(final Component component) {
    this.actionBars.setDefault(component);
    this.actionBars.renderTo(this.audience);
  }

  public void setActionBar(final String id, final Component component, final int priority) {
    this.actionBars.set(id, component, priority);
    this.actionBars.renderTo(this.audience);
  }

  public void clearActionBar(final String id) {
    this.actionBars.clear(id);
    this.actionBars.renderTo(this.audience);
  }

  public void clearAllActionBars() {
    this.actionBars.clearAll();
    this.actionBars.renderTo(this.audience);
  }

  public void showTitle(final Component title, final Component subtitle) {
    this.titles.setDefault(title(title, subtitle));
    this.titles.renderTo(this.audience);
  }

  public void showTitle(
      final Component title,
      final Component subtitle,
      final int in,
      final int stay,
      final int out) {
    this.titles.setDefault(title(title, subtitle, in, stay, out));
    this.titles.renderTo(this.audience);
  }

  public void showTitle(
      final String id,
      final Component title,
      final Component subtitle,
      final int priority,
      final int in,
      final int stay,
      final int out) {
    this.titles.set(id, title(title, subtitle, in, stay, out), priority);
    this.titles.renderTo(this.audience);
  }

  public void showTitle(
      final String id, final Component title, final Component subtitle, final int priority) {
    this.titles.set(id, title(title, subtitle), priority);
    this.titles.renderTo(this.audience);
  }

  public void clearTitle(final String id) {
    this.titles.clear(id);
    this.titles.renderTo(this.audience);
  }

  public void clearAllTitles() {
    this.titles.clearAll();
    this.titles.renderTo(this.audience);
  }

  public void showBossBar(
      final String id,
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay) {
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

  public void removeAllBossBars() {
    final Collection<BossBar> bars = this.bars.values();
    for (final BossBar bar : bars) {
      this.audience.hideBossBar(bar);
    }
  }

  private static final class LayeredActionBar {

    private final Map<String, Entry<Component>> layers;
    private final AtomicLong seq;
    private long defaultPriority;

    LayeredActionBar() {
      this.seq = new AtomicLong(0);
      this.layers = new HashMap<>();
    }

    void setDefault(final Component component) {
      this.defaultPriority++;
      final int safe = Math.clamp(this.defaultPriority, Integer.MIN_VALUE, Integer.MAX_VALUE);
      this.set(DEFAULT_LAYER_ID, component, safe);
    }

    void set(final String id, final Component component, final int priority) {
      final long seq = this.seq.incrementAndGet();
      final Entry<Component> entry = new Entry<>(component, priority, seq);
      this.layers.put(id, entry);
    }

    void clear(final String id) {
      this.layers.remove(id);
    }

    void clearAll() {
      this.layers.clear();
    }

    void renderTo(final Audience audience) {
      final Entry<Component> best = bestEntry(this.layers.values());
      if (best == null) {
        audience.sendActionBar(Component.empty());
        return;
      }
      audience.sendActionBar(best.payload());
    }
  }

  private static final class LayeredTitles {

    private final Map<String, Entry<Title>> layers;
    private final AtomicLong seq;
    private long defaultPriority;

    LayeredTitles() {
      this.seq = new AtomicLong(0);
      this.layers = new HashMap<>();
    }

    void setDefault(final Title title) {
      this.defaultPriority++;
      final int safe = Math.clamp(this.defaultPriority, Integer.MIN_VALUE, Integer.MAX_VALUE);
      this.set(DEFAULT_LAYER_ID, title, safe);
    }

    void set(final String id, final Title t, final int priority) {
      final long seq = this.seq.incrementAndGet();
      final Entry<Title> entry = new Entry<>(t, priority, seq);
      this.layers.put(id, entry);
    }

    void clear(final String id) {
      this.layers.remove(id);
    }

    void clearAll() {
      this.layers.clear();
    }

    void renderTo(final Audience audience) {
      final Entry<Title> best = bestEntry(this.layers.values());
      if (best == null) {
        audience.clearTitle();
        return;
      }
      audience.showTitle(best.payload());
    }
  }

  private record Entry<T>(T payload, int priority, long seq) {}

  private static @Nullable <T> Entry<T> bestEntry(final Collection<Entry<T>> entries) {
    Entry<T> best = null;
    for (final Entry<T> entry : entries) {
      if (best == null) {
        best = entry;
        continue;
      }
      final int priority = entry.priority();
      final long seq = entry.seq();
      final int bestPriority = best.priority();
      final long bestSeq = best.seq();
      if (priority > bestPriority || (priority == bestPriority && seq > bestSeq)) {
        best = entry;
      }
    }
    return best;
  }
}
