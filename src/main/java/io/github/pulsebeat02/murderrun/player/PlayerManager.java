package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.MurderGame;
import io.github.pulsebeat02.murderrun.config.GameConfiguration;
import io.github.pulsebeat02.murderrun.player.death.PlayerDeathManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public final class PlayerManager {

  private final MurderGame game;
  private final PlayerDeathManager deathManager;
  private final Map<UUID, GamePlayer> lookupMap;

  public PlayerManager(final MurderGame game) {
    this.game = game;
    this.deathManager = new PlayerDeathManager(game);
    this.lookupMap = new WeakHashMap<>();
  }

  public void start(final Collection<Player> murderers, final Collection<Player> participants) {
    this.assignPlayerRoles(murderers, participants);
    this.setupAllPlayers();
  }

  public void shutdown() {
    this.resetAllPlayers();
    this.deathManager.shutdownExecutor();
  }

  private void setupAllPlayers() {
    for (final GamePlayer player : this.getParticipants()) {
      player.onMatchStart();
    }
  }

  private void resetAllPlayers() {
    for (final GamePlayer player : this.getParticipants()) {
      player.onMatchReset();
    }
  }

  private void assignPlayerRoles(
      final Collection<Player> murderers, final Collection<Player> participants) {
    this.createMurderers(murderers);
    this.createInnocents(murderers, participants);
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

  private Set<UUID> createMurdererUuids(final Collection<Player> murderers) {
    return murderers.stream().map(Player::getUniqueId).collect(Collectors.toSet());
  }

  public Optional<GamePlayer> lookupPlayer(final UUID uuid) {
    return Optional.ofNullable(this.lookupMap.get(uuid));
  }

  public Collection<GamePlayer> getParticipants() {
    return this.lookupMap.values();
  }

  public Collection<Murderer> getMurderers() {
    return this.lookupMap.values().stream()
        .filter(player -> player instanceof Murderer)
        .map(murderer -> (Murderer) murderer)
        .collect(Collectors.toSet());
  }

  public Collection<InnocentPlayer> getInnocentPlayers() {
    return this.lookupMap.values().stream()
        .filter(player -> player instanceof InnocentPlayer)
        .map(murderer -> (InnocentPlayer) murderer)
        .collect(Collectors.toSet());
  }

  public MurderGame getGame() {
    return this.game;
  }

  public Collection<GamePlayer> getDead() {
    return this.lookupMap.values().stream()
        .filter(player -> !player.isAlive())
        .collect(Collectors.toSet());
  }

  public PlayerDeathManager getDeathManager() {
    return this.deathManager;
  }

  public void removePlayer(final UUID uuid) {
    this.lookupMap.remove(uuid);
  }
}
