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
import static net.kyori.adventure.text.Component.empty;

import fr.mrmicky.fastboard.adventure.FastBoard;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import me.brandonli.murderrun.game.*;
import me.brandonli.murderrun.game.arena.Arena;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.map.part.PartsManager;
import me.brandonli.murderrun.game.scheduler.GameScheduler;
import me.brandonli.murderrun.game.scheduler.reference.LoosePlayerReference;
import me.brandonli.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class PlayerScoreboard {

  private final GamePlayer gamePlayer;
  private final FastBoard board;
  private final AtomicReference<Component> distance;

  public PlayerScoreboard(final GamePlayer gamePlayer) {
    this.gamePlayer = gamePlayer;
    this.board = this.createSidebar(gamePlayer);
    this.distance = new AtomicReference<>(empty());
    this.startScheduler();
  }

  private void startScheduler() {
    final Game game = this.gamePlayer.getGame();
    final GameScheduler scheduler = game.getScheduler();
    final Runnable update = () -> {
      final Location current = this.gamePlayer.getLocation();
      final GameSettings settings = game.getSettings();
      final Arena arena = requireNonNull(settings.getArena());
      final Location truck = arena.getTruck();
      final int dist = (int) Math.ceil(current.distance(truck));
      final String dir = this.getDirection(truck, current);
      final Component component = Message.SCOREBOARD_TRUCK_SURVIVOR.build(dir, dist);
      this.distance.set(component);
      this.updateSidebar();
    };
    final LoosePlayerReference reference = LoosePlayerReference.of(this.gamePlayer);
    scheduler.scheduleRepeatedTask(update, 0L, 10L, reference);
  }

  private FastBoard createSidebar(@UnderInitialization PlayerScoreboard this, final GamePlayer gamePlayer) {
    return gamePlayer.applyFunction(FastBoard::new);
  }

  public void shutdown() {
    this.board.delete();
  }

  public void updateSidebar() {
    if (this.board.isDeleted()) {
      return;
    }

    this.board.updateTitle(this.generateTitleComponent());

    final boolean killer = this.gamePlayer instanceof Killer;

    final List<Component> lines = new ArrayList<>();
    lines.add(this.generateDateComponent());
    lines.add(empty());
    lines.add(this.generateModeComponent());
    lines.add(this.generateRoleComponent());
    lines.add(empty());
    lines.add(this.generatePartsComponent());
    lines.add(this.generateTimeComponent());

    if (!killer) {
      lines.add(this.distance.get());
    }

    lines.add(empty());
    lines.add(this.generateArenaComponent());
    lines.add(empty());
    lines.add(this.generateFooterComponent());

    this.board.updateLines(lines);
    //    if (killer) {
    //      this.board.updateLines(
    //          empty(),
    //          this.generateRoleComponent(),
    //          this.generateObjectiveComponent(),
    //          empty(),
    //          this.generatePartsComponent()
    //        );
    //    } else {
    //      this.board.updateLines(
    //          empty(),
    //          this.generateRoleComponent(),
    //          this.generateObjectiveComponent(),
    //          this.distance.get(),
    //          empty(),
    //          this.generatePartsComponent()
    //        );
    //    }
  }

  private Component generateFooterComponent() {
    return Message.GAME_SCOREBOARD_DOMAIN.build();
  }

  private Component generateArenaComponent() {
    final Game game = this.gamePlayer.getGame();
    final GameSettings settings = game.getSettings();
    final Arena arena = requireNonNull(settings.getArena());
    final String name = arena.getName();
    return Message.GAME_SCOREBOARD_MAP.build(name);
  }

  private Component generateTimeComponent() {
    final Game game = this.gamePlayer.getGame();
    final GameTimer timer = game.getTimeManager();
    final long ms = timer.getTimeLeft();
    final int seconds = (int) Math.ceil(ms / 1000.0);
    return Message.GAME_SCOREBOARD_TIME.build(seconds);
  }

  private Component generateModeComponent() {
    final Game game = this.gamePlayer.getGame();
    final GameMode mode = game.getMode();
    final String name = mode.getModeName();
    return Message.GAME_SCOREBOARD_MODE.build(name);
  }

  private Component generateDateComponent() {
    final ZoneId zoneId = ZoneId.systemDefault();
    final LocalDate now = LocalDate.now(zoneId);
    final int day = now.getDayOfMonth();
    final int month = now.getMonthValue();
    final int year = now.getYear();
    return Message.GAME_SCOREBOARD_DATE.build(day, month, year);
  }

  private String getDirection(final Location truck, final Location current) {
    final double tx = truck.getX() - current.getX();
    final double tz = truck.getZ() - current.getZ();
    if (tx == 0 && tz == 0) {
      return ".";
    }

    final Vector toTruck = new Vector(tx, 0, tz);
    final Vector normalized = toTruck.normalize();
    final Vector direction = current.getDirection();
    final Vector facing = direction.clone();
    facing.setY(0);
    if (facing.lengthSquared() == 0) {
      return ".";
    }
    facing.normalize();

    final Vector right = new Vector(-facing.getZ(), 0, facing.getX());
    final double forwardDot = facing.dot(normalized);
    final double rightDot = right.dot(normalized);
    if (Math.abs(forwardDot) >= Math.abs(rightDot)) {
      return forwardDot > 0 ? "↑" : "↓";
    } else {
      return rightDot > 0 ? "→" : "←";
    }
  }

  public Component generatePartsComponent() {
    final Game game = this.gamePlayer.getGame();
    final GameMap map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    int remaining = manager.getRemainingParts();
    if (remaining == 0) {
      final GameProperties properties = game.getProperties();
      remaining = properties.getCarPartsRequired();
    }
    return Message.SCOREBOARD_PARTS.build(remaining);
  }

  private Component generateObjectiveComponent() {
    final boolean killer = this.gamePlayer instanceof Killer;
    return killer ? Message.SCOREBOARD_OBJECTIVE_KILLER.build() : Message.SCOREBOARD_OBJECTIVE_SURVIVOR.build();
  }

  private Component generateRoleComponent() {
    final boolean killer = this.gamePlayer instanceof Killer;
    return killer ? Message.SCOREBOARD_ROLE_KILLER.build() : Message.SCOREBOARD_ROLE_SURVIVOR.build();
  }

  private Component generateTitleComponent() {
    return Message.SCOREBOARD_TITLE.build();
  }
}
