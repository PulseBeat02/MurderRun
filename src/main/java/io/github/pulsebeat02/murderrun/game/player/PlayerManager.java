package io.github.pulsebeat02.murderrun.game.player;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.KillerLocationTracker;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTool;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
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
        .filter(player -> !player.isAlive())
        .collect(Collectors.toSet());
    this.cachedSurvivors = this.lookupMap.values().stream()
        .filter(player -> player instanceof Survivor)
        .map(murderer -> (Survivor) murderer)
        .collect(Collectors.toSet());
  }

  public @Nullable GamePlayer getNearestKiller(final Location origin) {
    GamePlayer nearest = null;
    double min = Double.MAX_VALUE;
    final Collection<Killer> killers = this.getMurderers();
    for (final GamePlayer killer : killers) {
      final Location location = killer.getLocation();
      final double distance = location.distanceSquared(origin);
      if (distance < min) {
        nearest = killer;
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

  public void shutdown() {
    this.resetAllPlayers();
  }

  private void resetAllPlayers() {
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
}
