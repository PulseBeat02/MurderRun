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
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class PlayerAudience {

  private final Audience audience;
  private final Collection<BossBar> bars;

  public PlayerAudience(final Game game, final UUID uuid) {
    this.audience = this.getAudience(game, uuid);
    this.bars = new HashSet<>();
  }

  private Audience getAudience(
      @UnderInitialization PlayerAudience this, final Game game, final UUID uuid) {
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
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay) {
    final BossBar bar = bossBar(name, progress, color, overlay);
    this.bars.add(bar);
    this.audience.showBossBar(bar);
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

  public void removeAllBossBars() {
    for (final BossBar bar : this.bars) {
      this.audience.hideBossBar(bar);
    }
  }
}
