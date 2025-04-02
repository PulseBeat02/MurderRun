/*

MIT License

Copyright (c) 2024 Brandon Li

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/
package io.github.pulsebeat02.murderrun.game.player;

import static java.util.Objects.requireNonNull;

import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.player.death.KillerLocationTracker;
import io.github.pulsebeat02.murderrun.game.player.death.PlayerDeathTool;
import io.github.pulsebeat02.murderrun.game.player.phase.PlayerResetTool;
import io.github.pulsebeat02.murderrun.game.player.phase.PlayerStartupTool;
import io.github.pulsebeat02.murderrun.utils.StreamUtils;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class GamePlayerManager implements PlayerManagerHelper {

  private final Game game;
  private final PlayerDeathTool deathManager;
  private final KillerLocationTracker killerLocationTracker;
  private final MovementManager movementManager;
  private final LightManager lightManager;
  private final Map<UUID, GamePlayer> lookupMap;

  public GamePlayerManager(final Game game) {
    this.game = game;
    this.deathManager = new PlayerDeathTool(game);
    this.killerLocationTracker = new KillerLocationTracker(game);
    this.movementManager = new MovementManager(game);
    this.lightManager = new LightManager(game);
    this.lookupMap = new WeakHashMap<>();
  }

  public void start(final Collection<Player> murderers, final Collection<Player> participants) {
    this.assignPlayerRoles(murderers, participants);
    this.setupAllPlayers();
    this.lightManager.startLightChecks();
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

  private Set<UUID> createMurdererUuids(final Collection<Player> murderers) {
    return murderers.stream().map(Player::getUniqueId).collect(Collectors.toSet());
  }

  @Override
  public Stream<GamePlayer> getParticipants() {
    return this.lookupMap.values().stream();
  }

  @Override
  public void resetAllPlayers() {
    final PlayerResetTool manager = new PlayerResetTool(this);
    manager.configure();
  }

  @Override
  public Stream<GamePlayer> getSurvivors() {
    return this.getParticipants().filter(StreamUtils.isInstanceOf(Survivor.class));
  }

  @Override
  public Stream<GamePlayer> getLivingInnocentPlayers() {
    return this.getSurvivors().filter(GamePlayer::isAlive);
  }

  @Override
  public Stream<GamePlayer> getKillers() {
    return this.getParticipants().filter(StreamUtils.isInstanceOf(Killer.class));
  }

  @Override
  public Stream<GamePlayer> getDeceasedSurvivors() {
    return this.getSurvivors().filter(StreamUtils.inverse(GamePlayer::isAlive));
  }

  @Override
  public GamePlayer getGamePlayer(final UUID uuid) {
    return requireNonNull(this.lookupMap.get(uuid));
  }

  @Override
  public boolean checkPlayerExists(final UUID uuid) {
    return this.lookupMap.containsKey(uuid);
  }

  @Override
  public boolean checkPlayerExists(final Player player) {
    final UUID uuid = player.getUniqueId();
    return this.checkPlayerExists(uuid);
  }

  @Override
  public Game getGame() {
    return this.game;
  }

  @Override
  public PlayerDeathTool getDeathManager() {
    return this.deathManager;
  }

  @Override
  public @Nullable GamePlayer removePlayer(final UUID uuid) {
    return this.lookupMap.remove(uuid);
  }

  @Override
  public void promoteToKiller(final GamePlayer player) {
    final UUID uuid = player.getUUID();
    final Killer killer = new Killer(this.game, uuid);
    killer.start();
    this.lookupMap.put(uuid, killer);
  }

  @Override
  public MovementManager getMovementManager() {
    return this.movementManager;
  }

  @Override
  public LightManager getLightManager() {
    return this.lightManager;
  }
}
