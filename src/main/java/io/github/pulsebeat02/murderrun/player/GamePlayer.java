package io.github.pulsebeat02.murderrun.player;

import static net.kyori.adventure.title.Title.title;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.game.MurderSettings;
import io.github.pulsebeat02.murderrun.lobby.MurderLobby;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import io.github.pulsebeat02.murderrun.player.death.PlayerDeathManager;
import io.github.pulsebeat02.murderrun.utils.PlayerUtils;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract sealed class GamePlayer permits InnocentPlayer, Murderer {

  private final MurderGame game;
  private final UUID uuid;
  private boolean alive;

  public GamePlayer(final MurderGame game, final UUID uuid) {
    this.game = game;
    this.uuid = uuid;
    this.alive = true;
  }

  public abstract void onPlayerAttemptPickupPartEvent(final EntityPickupItemEvent event);

  public void onMatchStart() {
    final Player player = this.getPlayer();
    player.setGameMode(GameMode.ADVENTURE);
    player.setHealth(20f);
    player.setFoodLevel(20);
  }

  public Player getPlayer() {
    final Player player = Bukkit.getPlayer(this.uuid);
    if (player == null) {
      throw new AssertionError("Failed to retrieve Player object from uuid!");
    }
    return player;
  }

  public void onMatchReset() {
    final MurderSettings configuration = this.game.getSettings();
    final MurderLobby lobby = configuration.getLobby();
    final Location location = lobby.getLobbySpawn();
    final Player player = this.getPlayer();
    PlayerUtils.removeAllPotionEffects(player);
    player.getInventory().clear();
    player.setGameMode(GameMode.SURVIVAL);
    player.teleport(location);
    player.setHealth(20f);
    player.setFoodLevel(20);
    player.setWalkSpeed(0.2f);
    player.setExp(0);
    player.setLevel(0);
    player.setSaturation(Float.MAX_VALUE);
    this.removeAllBossBars();
  }

  public void removeAllBossBars() {
    final Player player = this.getPlayer();
    final Server server = Bukkit.getServer();
    final Iterator<KeyedBossBar> bars = server.getBossBars();
    while (bars.hasNext()) {
      final KeyedBossBar bar = bars.next();
      final List<Player> players = bar.getPlayers();
      if (!players.contains(player)) {
        continue;
      }
      bar.removePlayer(player);
    }
  }

  public void spawnParticle(final Particle particle, final Location location, final int count) {
    this.spawnParticle(particle, location, count, 0, 0, 0);
  }

  public void spawnParticle(
      final Particle particle,
      final Location location,
      final int count,
      final double offSetX,
      final double offSetY,
      final double offSetZ) {
    final Player player = this.getPlayer();
    player.spawnParticle(particle, location, count, offSetX, offSetY, offSetZ);
  }

  public void addPotionEffect(final PotionEffect effect) {
    final Player player = this.getPlayer();
    player.addPotionEffect(effect);
  }

  public void markDeath() {
    this.setAlive(false);
    this.startDeathSequence();
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

  public void setXpLevel(final int level) {
    final Player player = this.getPlayer();
    player.setLevel(level);
  }

  public PlayerInventory getInventory() {
    final Player player = this.getPlayer();
    return player.getInventory();
  }

  public void sendMessage(final Component component) {
    final Audience audience = this.getAudience();
    audience.sendMessage(component);
  }

  public Audience getAudience() {
    final MurderRun plugin = this.game.getPlugin();
    final AudienceHandler handler = plugin.getAudience();
    final BukkitAudiences audiences = handler.retrieve();
    return audiences.player(this.uuid);
  }

  public void showTitle(final Component title, final Component subtitle) {
    final Audience audience = this.getAudience();
    audience.showTitle(title(title, subtitle));
  }

  public void showBossBar(
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay) {
    final Audience audience = this.getAudience();
    final BossBar bar = BossBar.bossBar(name, progress, color, overlay);
    audience.showBossBar(bar);
  }

  public void teleport(final Location location) {
    final Player player = this.getPlayer();
    player.teleport(location);
  }

  public void playSound(
      final Location location,
      final String key,
      final SoundCategory category,
      final int volume,
      final int pitch) {
    final Player player = this.getPlayer();
    player.playSound(location, key, category, volume, pitch);
  }

  public void playSound(
      final Location location,
      final Sound key,
      final SoundCategory category,
      final int volume,
      final int pitch) {
    final Player player = this.getPlayer();
    player.playSound(location, key, category, volume, pitch);
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

  private void startDeathSequence() {
    final PlayerManager manager = this.game.getPlayerManager();
    final PlayerDeathManager death = manager.getDeathManager();
    death.initiateDeathSequence(this);
  }

  public MurderGame getGame() {
    return this.game;
  }
}
