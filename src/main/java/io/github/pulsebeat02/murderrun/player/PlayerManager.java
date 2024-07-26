package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.game.MurderGame;
import io.github.pulsebeat02.murderrun.player.death.MurdererLocationManager;
import io.github.pulsebeat02.murderrun.player.death.PlayerDeathManager;
import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PlayerManager {

  private final MurderGame game;
  private final PlayerDeathManager deathManager;
  private final MurdererLocationManager murdererLocationManager;

  private final Map<UUID, GamePlayer> lookupMap;
  private Collection<GamePlayer> cachedDeadPlayers;
  private Collection<Murderer> cachedMurderers;
  private Collection<InnocentPlayer> cachedInnocentPlayers;

  public PlayerManager(final MurderGame game) {
    this.game = game;
    this.deathManager = new PlayerDeathManager(game);
    this.murdererLocationManager = new MurdererLocationManager(game);
    this.lookupMap = new WeakHashMap<>();
  }

  public void start(final Collection<Player> murderers, final Collection<Player> participants) {
    this.assignPlayerRoles(murderers, participants);
    this.setupAllPlayers();
    this.resetCachedPlayers();
    this.murdererLocationManager.spawnParticles();
    this.deathManager.spawnParticles();
  }

  private void assignPlayerRoles(
      final Collection<Player> murderers, final Collection<Player> participants) {
    this.createMurderers(murderers);
    this.createInnocents(murderers, participants);
  }

  private void setupAllPlayers() {
    for (final GamePlayer player : this.getParticipants()) {
      player.onMatchStart();
    }
  }

  public void resetCachedPlayers() {
    this.cachedMurderers =
        this.lookupMap.values().stream()
            .filter(player -> player instanceof Murderer)
            .map(murderer -> (Murderer) murderer)
            .collect(Collectors.toSet());
    this.cachedDeadPlayers =
        this.lookupMap.values().stream()
            .filter(player -> !player.isAlive())
            .collect(Collectors.toSet());
    this.cachedInnocentPlayers =
        this.lookupMap.values().stream()
            .filter(player -> player instanceof InnocentPlayer)
            .map(murderer -> (InnocentPlayer) murderer)
            .collect(Collectors.toSet());
  }

  private void createMurderers(final Collection<Player> murderers) {
    for (final Player player : murderers) {
      final UUID uuid = player.getUniqueId();
      final Murderer murderer = new Murderer(this.game, uuid);
      this.lookupMap.put(uuid, murderer);
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
      final InnocentPlayer innocent = new InnocentPlayer(this.game, uuid);
      this.lookupMap.put(uuid, innocent);
    }
  }

  public Collection<GamePlayer> getParticipants() {
    return this.lookupMap.values();
  }

  private Set<UUID> createMurdererUuids(final Collection<Player> murderers) {
    return murderers.stream().map(Player::getUniqueId).collect(Collectors.toSet());
  }

  public void shutdown() {
    this.resetAllPlayers();
    this.deathManager.shutdownExecutor();
    this.murdererLocationManager.shutdownExecutor();
  }

  private void resetAllPlayers() {
    for (final GamePlayer player : this.getParticipants()) {
      player.onMatchReset();
    }
  }

  public Optional<GamePlayer> lookupPlayer(final UUID uuid) {
    return Optional.ofNullable(this.lookupMap.get(uuid));
  }

  public Collection<Murderer> getMurderers() {
    return this.cachedMurderers;
  }

  public Collection<InnocentPlayer> getInnocentPlayers() {
    return this.cachedInnocentPlayers;
  }

  public MurderGame getGame() {
    return this.game;
  }

  public Collection<GamePlayer> getDead() {
    return this.cachedDeadPlayers;
  }

  public PlayerDeathManager getDeathManager() {
    return this.deathManager;
  }

  public @Nullable GamePlayer removePlayer(final UUID uuid) {
    return this.lookupMap.remove(uuid);
  }
}
