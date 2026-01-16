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

  public void stopSound(final SoundStop stop) {
    this.audience.stopSound(stop);
  }

  public void setActionBar(final Component component) {
    this.audience.sendActionBar(component);
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

  public void showTitle(final Component title, final Component subtitle) {
    this.audience.showTitle(title(title, subtitle));
  }

  public void showTitle(final Component title, final Component subtitle, final int in, final int stay, final int out) {
    this.audience.showTitle(title(title, subtitle, in, stay, out));
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
