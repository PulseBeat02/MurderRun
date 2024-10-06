package io.github.pulsebeat02.murderrun.game.player;

import static net.kyori.adventure.text.Component.empty;

import fr.mrmicky.fastboard.adventure.FastBoard;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.GameProperties;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.initialization.qual.UnderInitialization;

public final class PlayerScoreboard {

  private final GamePlayer gamePlayer;
  private final FastBoard board;

  public PlayerScoreboard(final GamePlayer gamePlayer) {
    this.gamePlayer = gamePlayer;
    this.board = this.createSidebar(gamePlayer);
  }

  private FastBoard createSidebar(
      @UnderInitialization PlayerScoreboard this, final GamePlayer gamePlayer) {
    final Player player = gamePlayer.getInternalPlayer();
    return new FastBoard(player);
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
        this.generateRoleMetaComponent(),
        empty(),
        this.generateObjectiveComponent(),
        this.generateObjectiveMetaComponent(),
        empty(),
        this.generatePartsComponent(),
        this.generatePartsMetaComponent());
  }

  public Component generatePartsMetaComponent() {
    final Game game = this.gamePlayer.getGame();
    final Map map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    int remaining = manager.getRemainingParts();
    if (remaining == 0) {
      remaining = GameProperties.CAR_PARTS_COUNT;
    }
    return Message.SCOREBOARD_PARTS_COUNT.build(remaining);
  }

  private Component generatePartsComponent() {
    return Message.SCOREBOARD_PARTS.build();
  }

  private Component generateObjectiveMetaComponent() {
    final boolean killer = this.gamePlayer instanceof Killer;
    return killer
        ? Message.SCOREBOARD_OBJECTIVE_KILLER.build()
        : Message.SCOREBOARD_OBJECTIVE_SURVIVOR.build();
  }

  private Component generateObjectiveComponent() {
    return Message.SCOREBOARD_OBJECTIVE.build();
  }

  private Component generateRoleMetaComponent() {
    final boolean killer = this.gamePlayer instanceof Killer;
    return killer
        ? Message.SCOREBOARD_ROLE_KILLER.build()
        : Message.SCOREBOARD_ROLE_SURVIVOR.build();
  }

  private Component generateRoleComponent() {
    return Message.SCOREBOARD_ROLE.build();
  }

  private Component generateTitleComponent() {
    return Message.SCOREBOARD_TITLE.build();
  }
}
