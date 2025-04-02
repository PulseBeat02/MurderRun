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
      remaining = GameProperties.CAR_PARTS_COUNT;
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
