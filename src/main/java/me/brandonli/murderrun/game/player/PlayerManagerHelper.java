/*
 * This file is part of Murder Run, a spin-off game-mode of Dead by Daylight
 * Copyright (C) Brandon Li <https://brandonli.me/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.brandonli.murderrun.game.player;

import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.key.Key.key;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.player.death.PlayerDeathTool;
import me.brandonli.murderrun.game.player.metadata.MetadataManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.StrictPlayerReference;
import me.brandonli.murderrun.resourcepack.sound.SoundResource;
import me.brandonli.murderrun.utils.RandomUtils;
import me.brandonli.murderrun.utils.StreamUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.PolyNull;

public interface PlayerManagerHelper {
  int getTotalPlayers();

  void shutdown();

  default void applyToSurvivors(final Consumer<GamePlayer> consumer) {
    this.getSurvivors().forEach(consumer);
  }

  Stream<GamePlayer> getSurvivors();

  default void applyToLivingSurvivors(final Consumer<GamePlayer> consumer) {
    this.getLivingInnocentPlayers().forEach(consumer);
  }

  Stream<GamePlayer> getLivingInnocentPlayers();

  Stream<GamePlayer> getLivingKillerPlayers();

  default void applyToKillers(final Consumer<GamePlayer> consumer) {
    this.getKillers().forEach(consumer);
  }

  default void applyToLivingKillers(final Consumer<GamePlayer> consumer) {
    this.getLivingKillerPlayers().forEach(consumer);
  }

  Stream<GamePlayer> getKillers();

  default void applyToDeceased(final Consumer<GamePlayer> consumer) {
    this.getDeceasedSurvivors().forEach(consumer);
  }

  Stream<GamePlayer> getDeceasedSurvivors();

  default void applyToAllParticipants(final Consumer<GamePlayer> consumer) {
    this.getParticipants().forEach(consumer);
  }

  Stream<GamePlayer> getParticipants();

  GamePlayer getGamePlayer(final UUID uuid);

  default GamePlayer getGamePlayer(final Player player) {
    final UUID uuid = player.getUniqueId();
    return this.getGamePlayer(uuid);
  }

  default void sendMessageToAllDeceased(final Component message) {
    this.applyToDeceased(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.sendMessage(message);
      });
  }

  default void sendMessageToAllParticipants(final Component message) {
    this.applyToAllParticipants(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.sendMessage(message);
      });
  }

  default void sendMessageToAllLivingSurvivors(final Component message) {
    this.applyToLivingSurvivors(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.sendMessage(message);
      });
  }

  default void showTitleForAllInnocents(final Component title, final Component subtitle) {
    this.applyToSurvivors(innocent -> {
        final PlayerAudience audience = innocent.getAudience();
        audience.showTitle(title, subtitle);
      });
  }

  default void showTitleForAllMurderers(final Component title, final Component subtitle) {
    this.applyToKillers(murderer -> {
        final PlayerAudience audience = murderer.getAudience();
        audience.showTitle(title, subtitle);
      });
  }

  default void showTitleForAllParticipants(final Component title, final Component subtitle) {
    this.applyToAllParticipants(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.showTitle(title, subtitle);
      });
  }

  default void playSoundForAllParticipants(final SoundResource... keys) {
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

  default void playSoundForAllParticipants(final String... keys) {
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

  default void playSoundForAllMurderers(final SoundResource... keys) {
    final SoundResource key = this.getRandomKey(keys);
    this.applyToKillers(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.playSound(key);
      });
  }

  default void playSoundForAllInnocents(final SoundResource... keys) {
    final SoundResource key = this.getRandomKey(keys);
    this.applyToSurvivors(innocent -> {
        final PlayerAudience audience = innocent.getAudience();
        audience.playSound(key);
      });
  }

  default void stopSoundsForAllParticipants(final SoundResource key) {
    this.applyToAllParticipants(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.stopSound(key);
      });
  }

  default void playSoundForAllParticipantsAtLocation(final Location origin, final SoundResource... keys) {
    final SoundResource key = this.getRandomKey(keys);
    final Key id = key.getKey();
    final String raw = id.asString();
    final World world = requireNonNull(origin.getWorld());
    world.playSound(origin, raw, SoundCategory.MASTER, 1f, 1f);
  }

  default void updateBossBarForAllParticipants(final String id, final float progress) {
    this.applyToAllParticipants(player -> {
        final PlayerAudience audience = player.getAudience();
        audience.updateBossBar(id, progress);
      });
  }

  default GamePlayer getRandomAliveInnocentPlayer() {
    final List<GamePlayer> list = this.getSurvivors().filter(GamePlayer::isAlive).collect(StreamUtils.toShuffledList());
    if (list.isEmpty()) {
      throw new IllegalStateException("No alive innocent players available");
    }
    return list.getFirst();
  }

  default @Nullable GamePlayer getRandomDeadPlayer() {
    final List<GamePlayer> list =
      this.getDeceasedSurvivors().filter(StreamUtils.isInstanceOf(Survivor.class)).collect(StreamUtils.toShuffledList());
    return list.isEmpty() ? null : list.getFirst();
  }

  default void setEntityGlowingForAliveInnocents(final GamePlayer entity, final NamedTextColor color, final long duration) {
    final Game game = this.getGame();
    final GameScheduler scheduler = game.getScheduler();
    final StrictPlayerReference reference = StrictPlayerReference.of(entity);
    this.setEntityGlowingForAliveInnocents(entity, color);
    scheduler.scheduleTask(() -> this.removeEntityGlowingForAliveInnocents(entity, color), duration, reference);
  }

  default void setEntityGlowingForAliveInnocents(final GamePlayer entity, final NamedTextColor color) {
    this.applyToLivingSurvivors(innocent -> {
        final MetadataManager metadata = innocent.getMetadataManager();
        entity.apply(target -> metadata.setEntityGlowing(target, color, true));
      });
  }

  default void removeEntityGlowingForAliveInnocents(final GamePlayer entity, final NamedTextColor color) {
    this.applyToLivingSurvivors(innocent -> {
        final MetadataManager metadata = innocent.getMetadataManager();
        entity.apply(target -> metadata.setEntityGlowing(target, color, false));
      });
  }

  default void showBossBarForAllParticipants(
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

  @SuppressWarnings("all") // checker
  default Optional<@PolyNull Killer> getKillerWithMostKills() {
    return this.getKillers().map(Killer.class::cast).filter(Objects::nonNull).max(Comparator.comparingInt(Killer::getKills));
  }

  @SuppressWarnings("all") // checker
  default Optional<@PolyNull Survivor> getSurvivorWithMostCarPartsRetrieved() {
    return this.getSurvivors()
      .map(Survivor.class::cast)
      .filter(Objects::nonNull)
      .max(Comparator.comparingInt(Survivor::getCarPartsRetrieved));
  }

  default @Nullable GamePlayer getNearestKiller(final Location origin) {
    final Stream<GamePlayer> killers = this.getKillers();
    return this.getNearestGamePlayer0(origin, killers);
  }

  default @Nullable GamePlayer getNearestLivingSurvivor(final Location origin) {
    final Stream<GamePlayer> survivors = this.getLivingInnocentPlayers();
    return this.getNearestGamePlayer0(origin, survivors);
  }

  private @Nullable GamePlayer getNearestGamePlayer0(final Location origin, final Stream<GamePlayer> survivors) {
    double min = Double.MAX_VALUE;
    GamePlayer nearest = null;
    final World target = origin.getWorld();
    final Collection<GamePlayer> collection = survivors.toList();
    for (final GamePlayer survivor : collection) {
      final Location location = survivor.getLocation();
      final World world = location.getWorld();
      if (target != world) {
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

  default @Nullable GamePlayer getNearestDeadSurvivor(final Location origin) {
    GamePlayer nearest = null;
    double min = Double.MAX_VALUE;
    final Stream<GamePlayer> dead = this.getDeceasedSurvivors();
    final Collection<GamePlayer> collection = dead.toList();
    for (final GamePlayer survivor : collection) {
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

  boolean checkPlayerExists(UUID uuid);

  boolean checkPlayerExists(Player player);

  Game getGame();

  PlayerDeathTool getDeathManager();

  @Nullable
  GamePlayer removePlayer(UUID uuid);

  void promoteToKiller(GamePlayer player);

  MovementManager getMovementManager();

  LightManager getLightManager();
}
