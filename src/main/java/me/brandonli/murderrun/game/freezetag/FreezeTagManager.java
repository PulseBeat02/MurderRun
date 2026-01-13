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
package me.brandonli.murderrun.game.freezetag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.player.GamePlayer;
import me.brandonli.murderrun.game.player.GamePlayerManager;
import me.brandonli.murderrun.game.player.PlayerAudience;
import me.brandonli.murderrun.game.player.Survivor;
import me.brandonli.murderrun.game.player.death.DeathManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.LoosePlayerReference;
import me.brandonli.murderrun.game.scheduler.reference.NullReference;
import me.brandonli.murderrun.locale.Message;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class FreezeTagManager {

  private final Game game;
  private final Map<UUID, BukkitTask> revivalTimers;
  private final Map<UUID, BukkitTask> hologramTasks;
  private BukkitTask reviveUpdateTask;

  public FreezeTagManager(final Game game) {
    this.game = game;
    this.revivalTimers = new HashMap<>();
    this.hologramTasks = new HashMap<>();
    this.startReviveUpdateTask(game);
  }

  @SuppressWarnings("all") // checker
  private void startReviveUpdateTask(@UnderInitialization FreezeTagManager this, final Game game) {
    final GameScheduler scheduler = game.getScheduler();
    final NullReference reference = NullReference.of();
    this.reviveUpdateTask = scheduler.scheduleRepeatedTask(
      () -> {
        final GamePlayerManager manager = game.getPlayerManager();
        manager
          .getSurvivors()
          .filter(GamePlayer::isAlive)
          .map(p -> (Survivor) p)
          .filter(Survivor::isFrozen)
          .filter(s -> s.getRevivingPlayer() != null)
          .forEach(this::updateRevive);
      },
      0L,
      10L,
      reference
    );
  }

  public void freezeSurvivor(final Survivor survivor) {
    if (survivor.isFrozen()) {
      return;
    }

    survivor.setFrozen(true);
    survivor.setFrozenTime(System.currentTimeMillis());

    final Player player = survivor.getInternalPlayer();
    player.setGameMode(GameMode.SPECTATOR);
    player.setWalkSpeed(0f);
    player.setFlySpeed(0f);
    player.setAllowFlight(true);
    player.setGravity(false);

    final Component message = Message.FREEZE_TAG_FROZEN.build();
    survivor.getAudience().sendMessage(message);
    this.startRevivalTimer(survivor);
    this.startHologramTask(survivor);
  }

  public void unfreezeSurvivor(final Survivor survivor) {
    if (!survivor.isFrozen()) {
      return;
    }

    survivor.setFrozen(false);
    survivor.setRevivingPlayer(null);
    survivor.setReviveStartTime(0);

    final Player player = survivor.getInternalPlayer();
    player.setGameMode(GameMode.SURVIVAL);
    player.setWalkSpeed(0.2f);
    player.setFlySpeed(0.1f);

    final UUID uuid = survivor.getUUID();
    this.cancelRevivalTimer(uuid);
    this.cancelHologramTask(uuid);

    final DeathManager deathManager = survivor.getDeathManager();
    final NPC corpse = deathManager.getCorpse();
    if (corpse != null) {
      corpse.destroy();
      deathManager.setCorpse(null);
    }

    final Component message = Message.FREEZE_TAG_REVIVED.build();
    survivor.getAudience().sendMessage(message);
  }

  private void startRevivalTimer(final Survivor survivor) {
    final UUID uuid = survivor.getUUID();
    this.cancelRevivalTimer(uuid);

    final GameProperties properties = this.game.getProperties();
    final int reviveTimer = properties.getFreezeTagReviveTimer();
    final GameScheduler scheduler = this.game.getScheduler();
    final LoosePlayerReference reference = LoosePlayerReference.of(survivor);

    final BukkitTask task = scheduler.scheduleTask(
      () -> {
        if (survivor.isFrozen()) {
          this.killFrozenSurvivor(survivor);
        }
      },
      reviveTimer,
      reference
    );

    this.revivalTimers.put(uuid, task);
  }

  private void killFrozenSurvivor(final Survivor survivor) {
    survivor.setFrozen(false);
    survivor.setAlive(false);
    survivor.setWalkSpeed(0.2f);
    survivor.setFlySpeed(0.1f);
    survivor.setHealth(0.0);

    final Component message = Message.FREEZE_TAG_DIED.build();
    final PlayerAudience audience = survivor.getAudience();
    audience.sendMessage(message);

    final UUID uuid = survivor.getUUID();
    this.cancelRevivalTimer(uuid);
    this.cancelHologramTask(uuid);
  }

  private void cancelRevivalTimer(final UUID uuid) {
    final BukkitTask task = this.revivalTimers.remove(uuid);
    if (task != null) {
      task.cancel();
    }
  }

  private void startHologramTask(final Survivor survivor) {
    final UUID uuid = survivor.getUUID();
    this.cancelHologramTask(uuid);

    final GameScheduler scheduler = this.game.getScheduler();
    final LoosePlayerReference reference = LoosePlayerReference.of(survivor);
    final BukkitTask task = scheduler.scheduleRepeatedTask(() -> this.updateHologram(survivor), 0L, 10L, reference);
    this.hologramTasks.put(uuid, task);
  }

  private void updateHologram(final Survivor survivor) {
    if (!survivor.isFrozen()) {
      return;
    }

    final GameProperties properties = this.game.getProperties();
    final int reviveTimerTicks = properties.getFreezeTagReviveTimer();
    final int reviveTimerSeconds = reviveTimerTicks / 20;
    final long frozenTime = survivor.getFrozenTime();
    final long currentTime = System.currentTimeMillis();
    final long elapsed = (currentTime - frozenTime) / 1000;
    final long remaining = reviveTimerSeconds - elapsed;

    if (remaining <= 0) {
      return;
    }

    final String raw = String.valueOf(remaining);
    final Component hologramText = Message.FREEZE_TAG_HOLOGRAM.build(raw);
    final GamePlayerManager manager = this.game.getPlayerManager();
    manager
      .getSurvivors()
      .filter(GamePlayer::isAlive)
      .filter(p -> !p.equals(survivor))
      .map(GamePlayer::getAudience)
      .forEach(audience -> audience.setActionBar(hologramText));

    final Component timerText = Message.FREEZE_TAG_WAITING.build(raw);
    final PlayerAudience audience = survivor.getAudience();
    audience.setActionBar(timerText);
  }

  private void cancelHologramTask(final UUID uuid) {
    final BukkitTask task = this.hologramTasks.remove(uuid);
    if (task != null) {
      task.cancel();
    }
  }

  public void startRevive(final Survivor frozen, final Survivor reviver) {
    if (!frozen.isFrozen()) {
      return;
    }

    final UUID reviving = frozen.getRevivingPlayer();
    if (reviving != null) {
      return;
    }

    final UUID reviverId = reviver.getUUID();
    final long current = System.currentTimeMillis();
    frozen.setRevivingPlayer(reviverId);
    frozen.setReviveStartTime(current);

    final Component message = Message.FREEZE_TAG_REVIVING_START.build();
    final PlayerAudience reviverAudience = reviver.getAudience();
    final PlayerAudience frozenAudience = frozen.getAudience();
    frozenAudience.sendMessage(message);
    reviverAudience.sendMessage(message);
  }

  public void stopRevive(final Survivor frozen) {
    if (!frozen.isFrozen()) {
      return;
    }

    final UUID reviverId = frozen.getRevivingPlayer();
    if (reviverId == null) {
      return;
    }

    frozen.setRevivingPlayer(null);
    frozen.setReviveStartTime(0);

    final Component message = Message.FREEZE_TAG_REVIVING_STOPPED.build();
    final PlayerAudience frozenAudience = frozen.getAudience();
    frozenAudience.sendMessage(message);

    final GamePlayerManager manager = this.game.getPlayerManager();
    if (manager.checkPlayerExists(reviverId)) {
      final GamePlayer reviver = manager.getGamePlayer(reviverId);
      final PlayerAudience reviverAudience = reviver.getAudience();
      reviverAudience.sendMessage(message);
    }
  }

  public void updateRevive(final Survivor frozen) {
    if (!frozen.isFrozen()) {
      return;
    }

    final UUID reviverId = frozen.getRevivingPlayer();
    if (reviverId == null) {
      return;
    }

    final GamePlayerManager manager = this.game.getPlayerManager();
    if (!manager.checkPlayerExists(reviverId)) {
      this.stopRevive(frozen);
      return;
    }

    final GamePlayer reviverPlayer = manager.getGamePlayer(reviverId);
    final Player player = reviverPlayer.getInternalPlayer();

    if (!player.isSneaking() || !this.isNearCorpse(player.getLocation(), frozen)) {
      this.stopRevive(frozen);
      return;
    }

    final GameProperties properties = this.game.getProperties();
    final int revivalTimeTicks = properties.getFreezeTagRevivalTime();
    final int revivalTimeSeconds = revivalTimeTicks / 20;
    final long reviveStart = frozen.getReviveStartTime();
    final long currentTime = System.currentTimeMillis();
    final long elapsed = (currentTime - reviveStart) / 1000;

    if (elapsed >= revivalTimeSeconds) {
      this.unfreezeSurvivor(frozen);
      return;
    }

    final long remaining = revivalTimeSeconds - elapsed;
    final String raw = String.valueOf(remaining);
    final Component progressText = Message.FREEZE_TAG_REVIVING_PROGRESS.build(raw);

    final PlayerAudience frozenAudience = frozen.getAudience();
    frozenAudience.setActionBar(progressText);

    final PlayerAudience reviverAudience = reviverPlayer.getAudience();
    reviverAudience.setActionBar(progressText);
  }

  public boolean isNearCorpse(final Location playerLoc, final Survivor frozen) {
    final DeathManager deathManager = frozen.getDeathManager();
    final NPC corpse = deathManager.getCorpse();
    if (corpse == null) {
      return false;
    }

    final Entity entity = corpse.getEntity();
    if (entity == null || entity.isDead()) {
      return false;
    }

    final Location corpseLoc = entity.getLocation();
    final World playerWorld = playerLoc.getWorld();
    final World corpseWorld = corpseLoc.getWorld();
    if (playerWorld == null || !playerWorld.equals(corpseWorld)) {
      return false;
    }

    final GameProperties properties = this.game.getProperties();
    final double reviveDistance = properties.getFreezeTagReviveRadius();
    final double reviveDistanceSquared = reviveDistance * reviveDistance;
    return playerLoc.distanceSquared(corpseLoc) <= reviveDistanceSquared;
  }

  public boolean checkAllSurvivorsFrozen() {
    final GamePlayerManager manager = this.game.getPlayerManager();
    return manager.getSurvivors().filter(GamePlayer::isAlive).allMatch(survivor -> ((Survivor) survivor).isFrozen());
  }

  public void shutdown() {
    this.revivalTimers.values().forEach(BukkitTask::cancel);
    this.revivalTimers.clear();
    this.hologramTasks.values().forEach(BukkitTask::cancel);
    this.hologramTasks.clear();
    if (this.reviveUpdateTask != null) {
      this.reviveUpdateTask.cancel();
    }
  }

  public Game getGame() {
    return this.game;
  }
}
