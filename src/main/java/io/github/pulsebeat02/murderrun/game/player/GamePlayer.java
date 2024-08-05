package io.github.pulsebeat02.murderrun.game.player;

import static net.kyori.adventure.bossbar.BossBar.bossBar;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.title.Title.title;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract sealed class GamePlayer permits Innocent, Murderer {

  private final MurderGame game;
  private final UUID uuid;
  private final Audience audience;
  private boolean alive;

  public GamePlayer(final MurderGame game, final UUID uuid) {
    this.game = game;
    this.uuid = uuid;
    this.audience = this.getAudience();
    this.alive = true;
  }

  private Audience getAudience(@UnderInitialization GamePlayer this) {
    final MurderRun plugin = this.game.getPlugin();
    final AudienceHandler handler = plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    return audiences.player(this.uuid);
  }

  public abstract void onPlayerAttemptPickupPartEvent(final EntityPickupItemEvent event);

  public Player getPlayer() {
    final Player player = Bukkit.getPlayer(this.uuid);
    if (player == null) {
      throw new AssertionError("Failed to retrieve Player object from uuid!");
    }
    return player;
  }

  public void apply(final Consumer<Player> consumer) {
    final Player player = this.getPlayer();
    consumer.accept(player);
  }

  public void spawnParticle(
      final Particle particle,
      final Location location,
      final int count,
      final double offSetX,
      final double offSetY,
      final double offSetZ) {
    this.apply(
        player -> player.spawnParticle(particle, location, count, offSetX, offSetY, offSetZ));
  }

  public void addPotionEffects(final PotionEffect... effects) {
    this.apply(player -> {
      for (final PotionEffect effect : effects) {
        player.addPotionEffect(effect);
      }
    });
  }

  public @NonNull Location getLocation() {
    final Player player = this.getPlayer();
    final Location location = player.getLocation();
    if (location == null) {
      /*
      Curse you Bukkit! Why does OfflinePlayer#getLocation have @Nullable, but
      Player#getLocation has @NotNull? I have to implement this useless
      null-check as a result so the Checker Framework is satisfied. And
      I have to add other methods to wrap the Player object.
       */
      throw new AssertionError("Curse you Bukkit!");
    }
    return location;
  }

  public @Nullable Location getDeathLocation() {
    final Player player = this.getPlayer();
    return player.getLastDeathLocation();
  }

  public PlayerInventory getInventory() {
    final Player player = this.getPlayer();
    return player.getInventory();
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
    this.audience.showBossBar(bossBar(name, progress, color, overlay));
  }

  public void playSound(
      final Key key, final Source category, final float volume, final float pitch) {
    this.audience.playSound(sound(key, category, volume, pitch));
  }

  public void teleport(final Location location) {
    this.apply(player -> player.teleport(location));
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public boolean isAlive() {
    return this.alive;
  }

  public void setAlive(final boolean alive) {
    this.alive = alive;
  }

  public MurderGame getGame() {
    return this.game;
  }
}
