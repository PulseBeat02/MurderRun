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

  // TODO: Handle player log outs, when Player instance is bad, etc

  private final MurderGame game;
  private final PlayerDeathManager deathManager;
  private final List<Player> participants;

  private Map<UUID, GamePlayer> lookupMap;
  private Collection<GamePlayer> dead;
  private Collection<Murderer> murderers;
  private Collection<InnocentPlayer> innocentPlayers;

  public PlayerManager(final MurderGame game, final Collection<Player> participants) {
    this.game = game;
    this.deathManager = new PlayerDeathManager(game);
    this.participants = participants;
  }

  private Collection<Player> createWeakHashSet(final Collection<Player> collection) {
    Collections.newSetFromMap(
            new WeakHashMap<Object, Boolean>()
    );
  }

  public void start() {
    this.lookupMap = new HashMap<>();
    this.dead = new HashSet<>();
    this.murderers = this.chooseMurderers();
    this.innocentPlayers = this.chooseInnocents();
    this.deathManager.start();
  }

  public void shutdown() {
    this.deathManager.shutdown();
  }

  public void resetAllPlayers() {
    final GameConfiguration configuration = this.game.getConfiguration();
    final Location location = configuration.getLobbySpawn();
    for (final Player player : this.participants) {
      player.clearActivePotionEffects();
      player.getInventory().clear();
      player.setGameMode(GameMode.SURVIVAL);
      player.teleport(location);
    }
  }

  private Collection<Murderer> chooseMurderers() {
    Collections.shuffle(this.participants);
    final GameConfiguration configuration = this.game.getConfiguration();
    final int count = configuration.getMurdererCount();
    final Set<Murderer> set = new HashSet<>();
    for (int i = 0; i < count; i++) {
      final Player player = this.participants.get(i);
      final UUID uuid = player.getUniqueId();
      final Murderer murderer = new Murderer(this.game, uuid);
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
      final InnocentPlayer innocent = new InnocentPlayer(this.game, uuid);
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

  public List<Player> getParticipants() {
    return this.participants;
  }

  public Collection<Murderer> getMurderers() {
    return this.murderers;
  }

  public Collection<InnocentPlayer> getInnocentPlayers() {
    return this.innocentPlayers;
  }

  public void addDeadPlayer(final GamePlayer player) {
    this.dead.add(player);
  }

  public void resurrectDeadPlayer(final GamePlayer player) {
    this.dead.remove(player);
  }

  public MurderGame getGame() {
    return this.game;
  }

  public Map<UUID, GamePlayer> getLookupMap() {
    return this.lookupMap;
  }

  public Collection<GamePlayer> getDead() {
    return this.dead;
  }

  public PlayerDeathManager getDeathManager() {
    return this.deathManager;
  }
}
