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

import static net.kyori.adventure.text.Component.empty;

import fr.mrmicky.fastboard.adventure.FastBoard;
import me.brandonli.murderrun.game.Game;
import me.brandonli.murderrun.game.GameProperties;
import me.brandonli.murderrun.game.map.GameMap;
import me.brandonli.murderrun.game.map.part.PartsManager;
import me.brandonli.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class PlayerScoreboard {

  private final GamePlayer gamePlayer;
  private final FastBoard board;

  public PlayerScoreboard(final GamePlayer gamePlayer) {
    this.gamePlayer = gamePlayer;
    this.board = this.createSidebar(gamePlayer);
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
    this.board.updateLines(
        empty(),
        this.generateRoleComponent(),
        this.generateObjectiveComponent(),
        empty(),
        this.generatePartsComponent()
      );
  }

  public Component generatePartsComponent() {
    final Game game = this.gamePlayer.getGame();
    final GameMap map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    int remaining = manager.getRemainingParts();
    if (remaining == 0) {
      final GameProperties properties = game.getProperties();
      remaining = properties.getCarPartsCount();
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
