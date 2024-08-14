package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.key.Key.key;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.KillerLocationTracker;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTool;
import io.github.pulsebeat02.murderrun.resourcepack.sound.SoundKeys;
import io.github.pulsebeat02.murderrun.utils.RandomUtils;
import io.github.pulsebeat02.murderrun.utils.StreamUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PlayerManager {

  private final Game game;
  private final PlayerDeathTool deathManager;
  private final KillerLocationTracker killerLocationTracker;

  private final Map<UUID, GamePlayer> lookupMap;
  private Collection<GamePlayer> cachedDeadPlayers;
  private Collection<Killer> cachedKillers;
  private Collection<Survivor> cachedSurvivors;

  public PlayerManager(final Game game) {
    this.game = game;
    this.deathManager = new PlayerDeathTool(game);
    this.killerLocationTracker = new KillerLocationTracker(game);
    this.lookupMap = new WeakHashMap<>();
  }

  public void start(final Collection<Player> murderers, final Collection<Player> participants) {
    this.assignPlayerRoles(murderers, participants);
    this.setupAllPlayers();
    this.resetCachedPlayers();
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
    this.cachedKillers = this.lookupMap.values().stream()
        .filter(player -> player instanceof Killer)
        .map(murderer -> (Killer) murderer)
        .collect(Collectors.toSet());
    this.cachedDeadPlayers = this.lookupMap.values().stream()
        .filter(StreamUtils.inverse(GamePlayer::isAlive))
        .collect(Collectors.toSet());
    this.cachedSurvivors = this.lookupMap.values().stream()
        .filter(player -> player instanceof Survivor)
        .map(murderer -> (Survivor) murderer)
        .collect(Collectors.toSet());
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

  private void createMurderers(final Collection<Player> murderers) {
    for (final Player player : murderers) {
      final UUID uuid = player.getUniqueId();
      final Killer killer = new Killer(this.game, uuid);
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

  public void applyToAllInnocents(final Consumer<Survivor> consumer) {
    this.getInnocentPlayers().forEach(consumer);
  }

  public Collection<Survivor> getInnocentPlayers() {
    return this.cachedSurvivors;
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

  public Optional<GamePlayer> lookupPlayer(final UUID uuid) {
    return Optional.ofNullable(this.lookupMap.get(uuid));
  }

  public Optional<GamePlayer> lookupPlayer(final Player player) {
    return this.lookupPlayer(player.getUniqueId());
  }

  public Game getGame() {
    return this.game;
  }

  public PlayerDeathTool getDeathManager() {
    return this.deathManager;
  }

  public @Nullable GamePlayer removePlayer(final UUID uuid) {
    return this.lookupMap.remove(uuid);
  }

  public void sendMessageToAllParticipants(final Component message) {
    this.applyToAllParticipants(player -> player.sendMessage(message));
  }

  public void showTitleForAllInnocents(final Component title, final Component subtitle) {
    this.applyToAllInnocents(innocent -> innocent.showTitle(title, subtitle));
  }

  public void showTitleForAllMurderers(final Component title, final Component subtitle) {
    this.applyToAllMurderers(murderer -> murderer.showTitle(title, subtitle));
  }

  public void showTitleForAllParticipants(final Component title, final Component subtitle) {
    this.applyToAllParticipants(player -> player.showTitle(title, subtitle));
  }

  public void playSoundForAllParticipants(final SoundKeys... keys) {
    final String key = this.getRandomKey(keys);
    final Key id = key(key);
    this.applyToAllParticipants(player -> player.playSound(id, Source.MASTER, 1f, 1f));
  }

  private String getRandomKey(final SoundKeys... keys) {
    final int bound = keys.length;
    final int random = RandomUtils.generateInt(bound);
    final SoundKeys chosen = keys[random];
    return chosen.getSoundName();
  }

  public void playSoundForAllParticipants(final String... keys) {
    final String id = this.getRandomKey(keys);
    final Key key = key(id);
    this.applyToAllParticipants(player -> player.playSound(key, Source.MASTER, 1f, 1f));
  }

  private String getRandomKey(final String... keys) {
    final int bound = keys.length;
    final int random = RandomUtils.generateInt(bound);
    return keys[random];
  }

  public void playSoundForAllMurderers(final SoundKeys... keys) {
    final String key = this.getRandomKey(keys);
    final Key id = key(key);
    this.applyToAllDead(player -> {
      final Location location = player.getLocation();
      player.playSound(id, Source.MASTER, 1f, 1f);
    });
  }

  public void playSoundForAllInnocents(final SoundKeys... keys) {
    final String key = this.getRandomKey(keys);
    final Key id = key(key);
    this.applyToAllInnocents(innocent -> innocent.playSound(id, Source.MASTER, 1f, 1f));
  }

  public void playSoundForAllParticipantsAtLocation(
      final Location origin, final SoundKeys... keys) {
    final String key = this.getRandomKey(keys);
    final World world = requireNonNull(origin.getWorld());
    world.playSound(origin, key, SoundCategory.MASTER, 1f, 1f);
  }

  public void showBossBarForAllParticipants(
      final Component name,
      final float progress,
      final BossBar.Color color,
      final BossBar.Overlay overlay) {
    this.applyToAllParticipants(player -> player.showBossBar(name, progress, color, overlay));
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
}
