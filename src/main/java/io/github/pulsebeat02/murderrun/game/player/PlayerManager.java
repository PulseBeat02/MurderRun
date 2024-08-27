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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
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
  private Collection<Survivor> cachedAlivePlayers;
  private Collection<Survivor> cachedSurvivors;

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

  private void assignPlayerRoles(
      final Collection<Player> murderers, final Collection<Player> participants) {
    this.createMurderers(murderers);
    this.createInnocents(murderers, participants);
  }

  private void setupAllPlayers() {
    final PlayerStartupTool manager = new PlayerStartupTool(this);
    manager.configurePlayers();
  }

  public void resetCachedPlayers() {
    final Collection<GamePlayer> players = this.lookupMap.values();
    this.cachedKillers = players.stream()
        .filter(StreamUtils.isInstanceOf(Killer.class))
        .map(murderer -> (Killer) murderer)
        .collect(Collectors.toSet());
    this.cachedDeadPlayers = players.stream()
        .filter(StreamUtils.inverse(GamePlayer::isAlive))
        .collect(Collectors.toSet());
    this.cachedSurvivors = players.stream()
        .filter(player -> player instanceof Survivor)
        .map(murderer -> (Survivor) murderer)
        .collect(Collectors.toSet());
    this.cachedAlivePlayers =
        this.cachedSurvivors.stream().filter(GamePlayer::isAlive).collect(Collectors.toSet());
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

  private void createInnocents(
      final Collection<Player> murderers, final Collection<Player> participants) {
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
    this.cachedAlivePlayers.forEach(consumer);
  }

  public void applyToAllLivingInnocents(final Consumer<GamePlayer> consumer) {
    this.cachedAlivePlayers.forEach(consumer);
  }

  public Collection<Survivor> getInnocentPlayers() {
    return this.cachedSurvivors;
  }

  public Collection<Survivor> getAliveInnocentPlayers() {
    return this.cachedAlivePlayers;
  }

  public void applyToAllMurderers(final Consumer<Killer> consumer) {
    this.getMurderers().forEach(consumer);
  }

  public Collection<Killer> getMurderers() {
    return this.cachedKillers;
  }

  public void applyToAllDead(final Consumer<GamePlayer> consumer) {
    this.getDead().forEach(consumer);
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

  public void sendMessageToAllParticipants(final Component message) {
    this.applyToAllParticipants(player -> player.sendMessage(message));
  }

  public void sendMessageToAllSurvivors(final Component message) {
    this.applyToAllInnocents(player -> player.sendMessage(message));
  }

  public void sendMessageToAllKillers(final Component message) {
    this.applyToAllMurderers(player -> player.sendMessage(message));
  }

  public void showTitleForAllInnocents(final Component title, final Component subtitle) {
    this.applyToAllLivingInnocents(innocent -> innocent.showTitle(title, subtitle));
  }

  public void showTitleForAllMurderers(final Component title, final Component subtitle) {
    this.applyToAllMurderers(murderer -> murderer.showTitle(title, subtitle));
  }

  public void showTitleForAllParticipants(final Component title, final Component subtitle) {
    this.applyToAllParticipants(player -> player.showTitle(title, subtitle));
  }

  public void playSoundForAllParticipants(final SoundResource... keys) {
    final SoundResource key = this.getRandomKey(keys);
    this.applyToAllParticipants(player -> player.playSound(key));
  }

  private SoundResource getRandomKey(final SoundResource... keys) {
    final int bound = keys.length;
    final int random = RandomUtils.generateInt(bound);
    return keys[random];
  }

  public void playSoundForAllParticipants(final String... keys) {
    final Key key = this.getRandomKey(keys);
    this.applyToAllParticipants(player -> player.playSound(key));
  }

  private Key getRandomKey(final String... keys) {
    final int bound = keys.length;
    final int random = RandomUtils.generateInt(bound);
    final String element = keys[random];
    return key(element);
  }

  public void playSoundForAllMurderers(final SoundResource... keys) {
    final SoundResource key = this.getRandomKey(keys);
    this.applyToAllMurderers(player -> player.playSound(key));
  }

  public void playSoundForAllInnocents(final SoundResource... keys) {
    final SoundResource key = this.getRandomKey(keys);
    this.applyToAllInnocents(innocent -> innocent.playSound(key));
  }

  public void stopSoundsForAllParticipants(final SoundResource key) {
    final Key id = key.getKey();
    this.applyToAllParticipants(player -> player.stopSound(id));
  }

  public void playSoundForAllParticipantsAtLocation(
      final Location origin, final SoundResource... keys) {
    final SoundResource key = this.getRandomKey(keys);
    final Key id = key.getKey();
    final String raw = id.asString();
    final World world = requireNonNull(origin.getWorld());
    world.playSound(origin, raw, SoundCategory.MASTER, 1f, 1f);
  }

  public void showBossBarForAllParticipants(
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay) {
    this.applyToAllParticipants(player -> {
      player.removeAllBossBars();
      player.showBossBar(name, progress, color, overlay);
    });
  }

  public Survivor getRandomAliveInnocentPlayer() {
    final List<Survivor> list = this.cachedSurvivors.stream()
        .filter(Survivor::isAlive)
        .collect(StreamUtils.toShuffledList());
    return list.getFirst();
  }

  public Survivor getRandomDeadPlayer() {
    final List<Survivor> list = this.cachedSurvivors.stream()
        .filter(StreamUtils.inverse(Survivor::isAlive))
        .collect(StreamUtils.toShuffledList());
    return list.getFirst();
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

  public void setEntityGlowingForAliveInnocents(
      final GamePlayer entity, final ChatColor color, final long duration) {
    final GameScheduler scheduler = this.game.getScheduler();
    this.setEntityGlowingForAliveInnocents(entity, color);
    scheduler.scheduleTask(
        () -> this.removeEntityGlowingForAliveInnocents(entity, color), duration);
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
}
