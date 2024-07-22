package io.github.pulsebeat02.murderrun.player;

import io.github.pulsebeat02.murderrun.config.GameConfiguration;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public final class PlayerManager {

  // TODO: Handle player log outs, when Player instance is bad, etc

  private final GameConfiguration configuration;
  private final Map<UUID, GamePlayer> lookupMap;
  private final List<Player> participants;
  private final Collection<Murderer> murderers;
  private final Collection<InnocentPlayer> innocentPlayers;

  public PlayerManager(final GameConfiguration configuration, final List<Player> participants) {
    this.configuration = configuration;
    this.participants = participants;
    this.lookupMap = new HashMap<>();
    this.murderers = this.chooseMurderers();
    this.innocentPlayers = this.chooseInnocents();
  }

  public void resetAllPlayers() {
    final Location location = this.configuration.getSpawn();
    for (final Player player : this.participants) {
      player.clearActivePotionEffects();
      player.getInventory().clear();
      player.setGameMode(GameMode.SURVIVAL);
      player.teleport(location);
    }
  }

  private Collection<Murderer> chooseMurderers() {
    Collections.shuffle(this.participants);
    final int count = this.configuration.getMurdererCount();
    final Set<Murderer> set = new HashSet<>();
    for (int i = 0; i < count; i++) {
      final Player player = this.participants.get(i);
      final UUID uuid = player.getUniqueId();
      final Murderer murderer = new Murderer(uuid);
      set.add(murderer);
      this.lookupMap.put(uuid, murderer);
    }
    return set;
  }

  private Collection<InnocentPlayer> chooseInnocents() {
    final Set<InnocentPlayer> set = new HashSet<>();
    for (final Player player : this.participants) {
      final UUID uuid = player.getUniqueId();
      final boolean check = this.isMurderer(uuid);
      if (check) {
        continue;
      }
      final InnocentPlayer innocent = new InnocentPlayer(uuid);
      set.add(innocent);
      this.lookupMap.put(uuid, innocent);
    }
    return set;
  }

  private boolean isMurderer(final UUID uuid) {
    for (final Murderer murderer : this.murderers) {
      final UUID check = murderer.getUuid();
      if (uuid == check) {
        return true;
      }
    }
    return false;
  }

  public Optional<GamePlayer> lookupPlayer(final UUID uuid) {
    return Optional.ofNullable(this.lookupMap.get(uuid));
  }

  public GameConfiguration getConfiguration() {
    return this.configuration;
  }

  public List<Player> getParticipants() {
    return this.participants;
  }

  public Collection<Murderer> getMurderers() {
    return this.murderers;
  }

  public Collection<InnocentPlayer> getInnocentPlayers() {
    return this.innocentPlayers;
  }
}
