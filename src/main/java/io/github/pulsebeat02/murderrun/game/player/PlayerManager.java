package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.KillerLocationTracker;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTool;
import io.github.pulsebeat02.murderrun.game.scheduler.GameScheduler;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundResource;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import io.github.pulsebeat02.murderrun.utils.StreamUtils;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PlayerManager {

  private final Game game;
  private final PlayerDeathTool deathManager;
  private final KillerLocationTracker killerLocationTracker;
  private final MovementManager movementManager;

  private final Map<UUID, GamePlayer> lookupMap;
  private Collection<GamePlayer> cachedDeadPlayers;
  private Collection<Killer> cachedKillers;
  private Collection<Survivor> cachedAliveSurvivors;
  private Collection<Survivor> cachedSurvivors;
  private Collection<GamePlayer> cachedAlivePlayers;

  public PlayerManager(final Game game) {
    this.game = game;
    this.deathManager = new PlayerDeathTool(game);
    this.killerLocationTracker = new KillerLocationTracker(game);
    this.movementManager = new MovementManager(game);
    this.lookupMap = new WeakHashMap<>();
  }

  public void start(final Collection<Player> murderers, final Collection<Player> participants) {
    this.assignPlayerRoles(murderers, participants);
    this.resetCachedPlayers();
    this.setupAllPlayers();
    this.movementManager.start();
    this.killerLocationTracker.spawnParticles();
    this.deathManager.spawnParticles();
  }

  private void assignPlayerRoles(final Collection<Player> murderers, final Collection<Player> participants) {
    this.createMurderers(murderers);
    this.createInnocents(murderers, participants);
  }

  private void setupAllPlayers() {
    final PlayerStartupTool manager = new PlayerStartupTool(this);
    manager.configurePlayers();
  }

  public void resetCachedPlayers() {
    final Collection<GamePlayer> players = this.lookupMap.values();
    this.cachedKillers = players
      .stream()
      .filter(StreamUtils.isInstanceOf(Killer.class))
      .map(murderer -> (Killer) murderer)
      .collect(StreamUtils.toSynchronizedSet());
    this.cachedDeadPlayers = players.stream().filter(StreamUtils.inverse(GamePlayer::isAlive)).collect(StreamUtils.toSynchronizedSet());
    this.cachedSurvivors = players
      .stream()
      .filter(player -> player instanceof Survivor)
      .map(murderer -> (Survivor) murderer)
      .collect(StreamUtils.toSynchronizedSet());
    this.cachedAlivePlayers = players.stream().filter(GamePlayer::isAlive).collect(StreamUtils.toSynchronizedSet());
    this.cachedAliveSurvivors = this.cachedSurvivors.stream().filter(GamePlayer::isAlive).collect(StreamUtils.toSynchronizedSet());
  }

  public @Nullable GamePlayer getNearestKiller(final Location origin) {
    GamePlayer nearest = null;
    double min = Double.MAX_VALUE;
    for (final GamePlayer killer : this.cachedKillers) {
      final Location location = killer.getLocation();
      final double distance = location.distanceSquared(origin);
      if (distance < min) {
        nearest = killer;
        min = distance;
      }
    }
    return nearest;
  }

  public @Nullable GamePlayer getNearestSurvivor(final Location origin) {
    GamePlayer nearest = null;
    double min = Double.MAX_VALUE;
    for (final GamePlayer survivor : this.cachedSurvivors) {
      final Location location = survivor.getLocation();
      final double distance = location.distanceSquared(origin);
      if (distance < min) {
        nearest = survivor;
        min = distance;
      }
    }
    return nearest;
  }

  public @Nullable GamePlayer getNearestDeadSurvivor(final Location origin) {
    GamePlayer nearest = null;
    double min = Double.MAX_VALUE;
    for (final GamePlayer survivor : this.cachedDeadPlayers) {
      final Location location = survivor.getDeathLocation();
      if (location == null) {
        continue;
      }
      final double distance = location.distanceSquared(origin);
      if (distance < min) {
        nearest = survivor;
        min = distance;
      }
    }
    return nearest;
  }

  private void createMurderers(final Collection<Player> murderers) {
    for (final Player player : murderers) {
      final UUID uuid = player.getUniqueId();
      final Killer killer = new Killer(this.game, uuid);
      killer.start();
      this.lookupMap.put(uuid, killer);
    }
  }

  private void createInnocents(final Collection<Player> murderers, final Collection<Player> participants) {
    final Set<UUID> uuids = this.createMurdererUuids(murderers);
    for (final Player player : participants) {
      final UUID uuid = player.getUniqueId();
      if (uuids.contains(uuid)) {
        continue;
      }
      final Survivor survivor = new Survivor(this.game, uuid);
      survivor.start();
      this.lookupMap.put(uuid, survivor);
    }
  }

  public void applyToAllParticipants(final Consumer<GamePlayer> consumer) {
    this.getParticipants().forEach(consumer);
  }

  private Set<UUID> createMurdererUuids(final Collection<Player> murderers) {
    return murderers.stream().map(Player::getUniqueId).collect(Collectors.toSet());
  }

  public Collection<GamePlayer> getParticipants() {
    return this.lookupMap.values();
  }

  public void resetAllPlayers() {
    final PlayerResetTool manager = new PlayerResetTool(this);
    manager.configure();
  }

  public void applyToAllInnocents(final Consumer<GamePlayer> consumer) {
    this.cachedSurvivors.forEach(consumer);
  }

  public void applyToAllLivingInnocents(final Consumer<GamePlayer> consumer) {
    this.cachedAliveSurvivors.forEach(consumer);
  }

  public void applyToAllLivingParticipants(final Consumer<GamePlayer> consumer) {
    this.cachedAlivePlayers.forEach(consumer);
  }

  public Collection<Survivor> getInnocentPlayers() {
    return this.cachedSurvivors;
  }

  public Collection<Survivor> getAliveInnocentPlayers() {
    return this.cachedAliveSurvivors;
  }

  public void applyToAllMurderers(final Consumer<GamePlayer> consumer) {
    this.cachedKillers.forEach(consumer);
  }

  public Collection<Killer> getMurderers() {
    return this.cachedKillers;
  }

  public void applyToAllDead(final Consumer<GamePlayer> consumer) {
    this.cachedDeadPlayers.forEach(consumer);
  }

  public Collection<GamePlayer> getDead() {
    return this.cachedDeadPlayers;
  }

  public GamePlayer getGamePlayer(final Player player) {
    final UUID uuid = player.getUniqueId();
    return this.getGamePlayer(uuid);
  }

  public GamePlayer getGamePlayer(final UUID uuid) {
    return requireNonNull(this.lookupMap.get(uuid));
  }

  public boolean checkPlayerExists(final UUID uuid) {
    return this.lookupMap.containsKey(uuid);
  }

  public boolean checkPlayerExists(final Player player) {
    final UUID uuid = player.getUniqueId();
    return this.checkPlayerExists(uuid);
  }

  public Game getGame() {
    return this.game;
  }

  public PlayerDeathTool getDeathManager() {
    return this.deathManager;
  }

  public @Nullable GamePlayer removePlayer(final UUID uuid) {
    final GamePlayer result = this.lookupMap.remove(uuid);
    this.resetCachedPlayers();
    return result;
  }

  public void sendMessageToAllDeadParticipants(final Component message) {
    this.applyToAllDead(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.sendMessage(message);
      });
  }

  public void sendMessageToAllParticipants(final Component message) {
    this.applyToAllParticipants(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.sendMessage(message);
      });
  }

  public void sendMessageToAllSurvivors(final Component message) {
    this.applyToAllLivingInnocents(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.sendMessage(message);
      });
  }

  public void sendMessageToAllKillers(final Component message) {
    this.applyToAllMurderers(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.sendMessage(message);
      });
  }

  public void showTitleForAllInnocents(final Component title, final Component subtitle) {
    this.applyToAllInnocents(innocent -> {
        final PlayerAudience audience = innocent.getAudience();
        audience.showTitle(title, subtitle);
      });
  }

  public void showTitleForAllMurderers(final Component title, final Component subtitle) {
    this.applyToAllMurderers(murderer -> {
        final PlayerAudience audience = murderer.getAudience();
        audience.showTitle(title, subtitle);
      });
  }

  public void showTitleForAllParticipants(final Component title, final Component subtitle) {
    this.applyToAllParticipants(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.showTitle(title, subtitle);
      });
  }

  public void playSoundForAllParticipants(final SoundResource... keys) {
    final SoundResource key = this.getRandomKey(keys);
    this.applyToAllParticipants(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.playSound(key);
      });
  }

  private SoundResource getRandomKey(final SoundResource... keys) {
    final int bound = keys.length;
    final int random = RandomUtils.generateInt(bound);
    return keys[random];
  }

  public void playSoundForAllParticipants(final String... keys) {
    final Key key = this.getRandomKey(keys);
    this.applyToAllParticipants(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.playSound(key);
      });
  }

  private Key getRandomKey(final String... keys) {
    final int bound = keys.length;
    final int random = RandomUtils.generateInt(bound);
    final String element = keys[random];
    return key(element);
  }

  public void playSoundForAllMurderers(final SoundResource... keys) {
    final SoundResource key = this.getRandomKey(keys);
    this.applyToAllMurderers(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.playSound(key);
      });
  }

  public void playSoundForAllInnocents(final SoundResource... keys) {
    final SoundResource key = this.getRandomKey(keys);
    this.applyToAllInnocents(innocent -> {
        final PlayerAudience audience = innocent.getAudience();
        audience.playSound(key);
      });
  }

  public void stopSoundsForAllParticipants(final SoundResource key) {
    this.applyToAllParticipants(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.stopSound(key);
      });
  }

  public void playSoundForAllParticipantsAtLocation(final Location origin, final String... keys) {
    final Key key = this.getRandomKey(keys);
    final String raw = key.asString();
    final World world = requireNonNull(origin.getWorld());
    world.playSound(origin, raw, SoundCategory.MASTER, 1f, 1f);
  }

  public void playSoundForAllParticipantsAtLocation(final Location origin, final SoundResource... keys) {
    final SoundResource key = this.getRandomKey(keys);
    final Key id = key.getKey();
    final String raw = id.asString();
    final World world = requireNonNull(origin.getWorld());
    world.playSound(origin, raw, SoundCategory.MASTER, 1f, 1f);
  }

  public void showBossBarForAllParticipants(
    final String id,
    final Component name,
    final float progress,
    final BossBar.Color color,
    final BossBar.Overlay overlay
  ) {
    this.applyToAllParticipants(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.removeAllBossBars();
        audience.showBossBar(id, name, progress, color, overlay);
      });
  }

  public void updateBossBarForAllParticipants(final String id, final float progress) {
    this.applyToAllParticipants(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.updateBossBar(id, progress);
      });
  }

  public Survivor getRandomAliveInnocentPlayer() {
    final List<Survivor> list = this.cachedSurvivors.stream().filter(Survivor::isAlive).collect(StreamUtils.toShuffledList());
    return list.getFirst();
  }

  public @Nullable Survivor getRandomDeadPlayer() {
    final List<Survivor> list =
      this.cachedDeadPlayers.stream()
        .filter(StreamUtils.isInstanceOf(Survivor.class))
        .map(player -> (Survivor) player)
        .collect(StreamUtils.toShuffledList());
    return list.isEmpty() ? null : list.getFirst();
  }

  public void promoteToKiller(final GamePlayer player) {
    final UUID uuid = player.getUUID();
    final Killer killer = new Killer(this.game, uuid);
    killer.start();
    this.lookupMap.put(uuid, killer);
    this.resetCachedPlayers();
  }

  public MovementManager getMovementManager() {
    return this.movementManager;
  }

  public void setEntityGlowingForAliveInnocents(final GamePlayer entity, final ChatColor color, final long duration) {
    final GameScheduler scheduler = this.game.getScheduler();
    this.setEntityGlowingForAliveInnocents(entity, color);
    scheduler.scheduleTask(() -> this.removeEntityGlowingForAliveInnocents(entity, color), duration);
  }

  public void setEntityGlowingForAliveInnocents(final GamePlayer entity, final ChatColor color) {
    this.applyToAllLivingInnocents(innocent -> {
        final MetadataManager metadata = innocent.getMetadataManager();
        entity.apply(target -> metadata.setEntityGlowing(target, color, true));
      });
  }

  public void removeEntityGlowingForAliveInnocents(final GamePlayer entity, final ChatColor color) {
    this.applyToAllLivingInnocents(innocent -> {
        final MetadataManager metadata = innocent.getMetadataManager();
        entity.apply(target -> metadata.setEntityGlowing(target, color, false));
      });
  }

  public void hideNameTagForAliveInnocents(final long ticks) {
    final GameScheduler scheduler = this.game.getScheduler();
    this.applyToAllLivingInnocents(innocent -> {
        final MetadataManager metadata = innocent.getMetadataManager();
        metadata.hideNameTag(scheduler, ticks);
      });
  }

  public Killer getKillerWithMostKills() {
    return this.cachedKillers.stream().max(Comparator.comparingInt(Killer::getKills)).orElseThrow();
  }

  public Survivor getSurvivorWithMostCarPartsRetrieved() {
    return this.cachedSurvivors.stream().max(Comparator.comparingInt(Survivor::getCarPartsRetrieved)).orElseThrow();
  }
}
