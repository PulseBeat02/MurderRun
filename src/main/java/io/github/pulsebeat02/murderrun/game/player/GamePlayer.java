package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.bossbar.BossBar.bossBar;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.title.Title.title;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTask;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.reflect.PacketToolsProvider;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public abstract sealed class GamePlayer implements Participant permits Survivor, Killer {

  private final Game game;
  private final UUID uuid;
  private final Audience audience;
  private final Collection<PlayerDeathTask> tasks;
  private boolean alive;

  public GamePlayer(final Game game, final UUID uuid) {
    this.game = game;
    this.uuid = uuid;
    this.audience = this.getAudience(game, uuid);
    this.alive = true;
    this.tasks = new HashSet<>();
  }

  private Audience getAudience(
      @UnderInitialization GamePlayer this, final Game game, final UUID uuid) {
    final MurderRun plugin = game.getPlugin();
    final AudienceProvider handler = plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    return audiences.player(uuid);
  }

  @Override
  public Player getInternalPlayer() {
    return requireNonNull(Bukkit.getPlayer(this.uuid));
  }

  @Override
  public void sendMessage(final Component component) {
    this.audience.sendMessage(component);
  }

  @Override
  public void showTitle(final Component title, final Component subtitle) {
    this.audience.showTitle(title(title, subtitle));
  }

  @Override
  public void showBossBar(
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay) {
    this.audience.showBossBar(bossBar(name, progress, color, overlay));
  }

  @Override
  public void playSound(
      final Key key, final Source category, final float volume, final float pitch) {
    this.audience.playSound(sound(key, category, volume, pitch));
  }

  @Override
  public void setEntityGlowingForPlayer(final Entity entity) {
    this.apply(innocent -> PacketToolsProvider.INSTANCE.sendGlowPacket(innocent, entity));
  }

  @Override
  public void removeEntityGlowingForPlayer(final Entity entity) {
    this.apply(innocent -> PacketToolsProvider.INSTANCE.sendRemoveGlowPacket(innocent, entity));
  }

  @Override
  public void addDeathTask(final PlayerDeathTask task) {
    this.tasks.add(task);
  }

  @Override
  public void removeDeathTask(final PlayerDeathTask task) {
    this.tasks.remove(task);
  }

  @Override
  public Collection<PlayerDeathTask> getDeathTasks() {
    return this.tasks;
  }

  @Override
  public UUID getUuid() {
    return this.uuid;
  }

  @Override
  public boolean isAlive() {
    return this.alive;
  }

  @Override
  public void setAlive(final boolean alive) {
    this.alive = alive;
  }

  @Override
  public Game getGame() {
    return this.game;
  }
}
