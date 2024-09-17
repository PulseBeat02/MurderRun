package io.github.pulsebeat02.murderrun.game.player;

import static net.kyori.adventure.text.Component.empty;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.Game;
import io.github.pulsebeat02.murderrun.game.map.Map;
import io.github.pulsebeat02.murderrun.game.map.part.PartsManager;
import io.github.pulsebeat02.murderrun.locale.Message;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.entity.Player;

public final class PlayerScoreboard {

  private final GamePlayer gamePlayer;
  private final Sidebar sidebar;

  public PlayerScoreboard(final GamePlayer gamePlayer) {
    this.gamePlayer = gamePlayer;
    this.sidebar = this.createSidebar(gamePlayer);
  }

  private Sidebar createSidebar(final GamePlayer gamePlayer) {
    final Game game = gamePlayer.getGame();
    final MurderRun plugin = game.getPlugin();
    final ScoreboardLibrary library = plugin.getScoreboardLibrary();
    final Sidebar sidebar = library.createSidebar();
    final Player player = gamePlayer.getInternalPlayer();
    sidebar.addPlayer(player);
    return sidebar;
  }

  public void initializeSidebar() {
    this.addTitle();
    this.emptyLine(0);
    this.addRoleTitle();
    this.addRole();
    this.emptyLine(3);
    this.addObjectiveTitle();
    this.addObjective();
    this.emptyLine(6);
    this.addPartsTitle();
    this.addPartsCount();
  }

  public void addPartsCount() {
    final Game game = this.gamePlayer.getGame();
    final Map map = game.getMap();
    final PartsManager manager = map.getCarPartManager();
    final int remaining = manager.getRemainingParts();
    final Component msg = Message.SCOREBOARD_PARTS_COUNT.build(remaining);
    this.sidebar.line(8, msg);
  }

  private void addPartsTitle() {
    final Component msg = Message.SCOREBOARD_PARTS.build();
    this.sidebar.line(9, msg);
  }

  private void addObjective() {
    final boolean killer = this.gamePlayer instanceof Killer;
    final Component msg;
    if (killer) {
      msg = Message.SCOREBOARD_OBJECTIVE_KILLER.build();
    } else {
      msg = Message.SCOREBOARD_OBJECTIVE_SURVIVOR.build();
    }
    this.sidebar.line(6, msg);
  }

  private void addObjectiveTitle() {
    final Component msg = Message.SCOREBOARD_OBJECTIVE.build();
    this.sidebar.line(5, msg);
  }

  private void addRole() {
    final boolean killer = this.gamePlayer instanceof Killer;
    final Component msg;
    if (killer) {
      msg = Message.SCOREBOARD_ROLE_KILLER.build();
    } else {
      msg = Message.SCOREBOARD_ROLE_SURVIVOR.build();
    }
    this.sidebar.line(2, msg);
  }

  private void addRoleTitle() {
    final Component msg = Message.SCOREBOARD_ROLE.build();
    this.sidebar.line(3, msg);
  }

  private void addTitle() {
    final Component msg = Message.SCOREBOARD_TITLE.build();
    this.sidebar.title(msg);
  }

  private void emptyLine(final int index) {
    this.sidebar.line(index, empty());
  }
}
