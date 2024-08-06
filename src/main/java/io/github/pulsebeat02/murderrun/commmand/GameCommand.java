package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.game.GameManager;
import io.github.pulsebeat02.murderrun.game.GameResult;
import io.github.pulsebeat02.murderrun.game.GameSettings;
import io.github.pulsebeat02.murderrun.game.arena.Arena;
import io.github.pulsebeat02.murderrun.game.arena.ArenaManager;
import io.github.pulsebeat02.murderrun.game.lobby.Lobby;
import io.github.pulsebeat02.murderrun.game.lobby.LobbyManager;
import io.github.pulsebeat02.murderrun.locale.AudienceProvider;
import io.github.pulsebeat02.murderrun.locale.Locale;
import java.util.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.type.tuple.Pair;

@SuppressWarnings("nullness")
public final class GameCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;
  private Map<Player, Pair<GameManager, Boolean>> games;
  private Map<Player, Collection<Player>> invites;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.games = new WeakHashMap<>();
    this.invites = new WeakHashMap<>();
    this.plugin = plugin;
  }

  @CommandDescription("murder_run.command.game.create.info")
  @Command(value = "murder game start", requiredSender = Player.class)
  public void startGame(final Player sender) {

    final Audience audience = this.audiences.player(sender);
    final Pair<GameManager, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data) || this.checkIfNotOwner(audience, data)) {
      return;
    }

    final GameManager manager = data.first();
    final Collection<Player> players = manager.getParticipants();
    if (this.checkIfNotEnoughPlayers(audience, players)) {
      return;
    }
    manager.startGame();

    final Component message = Locale.GAME_START.build();
    audience.sendMessage(message);
  }

  private boolean checkIfNotEnoughPlayers(
      final Audience audience, final Collection<Player> players) {
    final int size = players.size();
    if (size < 2) {
      final Component message = Locale.GAME_LOW_PLAYER_COUNT_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @CommandDescription("murder_run.command.game.create.info")
  @Command(value = "murder game create <arenaName> <lobbyName>", requiredSender = Player.class)
  public void createGame(
      final Player sender,
      @Argument(suggestions = "arena-suggestions") @Quoted final String arenaName,
      @Argument(suggestions = "lobby-suggestions") @Quoted final String lobbyName) {

    final Audience audience = this.audiences.player(sender);
    if (this.checkIfAlreadyInGame(audience, sender)
        || this.checkIfArenaValid(audience, arenaName)
        || this.checkIfLobbyValid(audience, lobbyName)) {
      return;
    }

    final ArenaManager arenaManager = this.plugin.getArenaManager();
    final LobbyManager lobbyManager = this.plugin.getLobbyManager();
    final Arena arena = arenaManager.getArena(arenaName);
    final Lobby lobby = lobbyManager.getLobby(lobbyName);
    final GameManager manager = new GameManager(this.plugin);
    manager.addParticipantToLobby(sender);

    final GameSettings settings = manager.getSettings();
    settings.setArena(arena);
    settings.setLobby(lobby);

    final Component message = Locale.GAME_CREATED.build();
    audience.sendMessage(message);
  }

  private boolean checkIfAlreadyInGame(final Audience audience, final Player sender) {
    final Pair<GameManager, Boolean> data = this.games.get(sender);
    if (data != null) {
      final Component message = Locale.GAME_CREATE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean checkIfArenaValid(final Audience audience, final String arenaName) {
    final ArenaManager arenaManager = this.plugin.getArenaManager();
    final Arena arena = arenaManager.getArena(arenaName);
    if (arena == null) {
      final Component message = Locale.GAME_ARENA_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean checkIfLobbyValid(final Audience audience, final String lobbyName) {
    final LobbyManager lobbyManager = this.plugin.getLobbyManager();
    final Lobby lobby = lobbyManager.getLobby(lobbyName);
    if (lobby == null) {
      final Component message = Locale.GAME_LOBBY_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public void setPlugin(final MurderRun plugin) {
    this.plugin = plugin;
  }

  public BukkitAudiences getAudiences() {
    return this.audiences;
  }

  public void setAudiences(final BukkitAudiences audiences) {
    this.audiences = audiences;
  }

  public Map<Player, Pair<GameManager, Boolean>> getGames() {
    return this.games;
  }

  public void setGames(final Map<Player, Pair<GameManager, Boolean>> games) {
    this.games = games;
  }

  public Map<Player, Collection<Player>> getInvites() {
    return this.invites;
  }

  public void setInvites(final Map<Player, Collection<Player>> invites) {
    this.invites = invites;
  }

  @CommandDescription("murder_run.command.game.cancel.info")
  @Command(value = "murder game cancel", requiredSender = Player.class)
  public void cancelGame(final Player sender) {

    final Audience audience = this.audiences.player(sender);
    final Pair<GameManager, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data) || this.checkIfNotOwner(audience, data)) {
      return;
    }

    final GameManager manager = data.first();
    manager.getGame().finishGame(GameResult.INTERRUPTED);

    final Collection<Player> participants = manager.getParticipants();
    final Component ownerMessage = Locale.GAME_CANCEL.build();
    final Component kickedMessage = Locale.GAME_PLAYER_KICK.build();
    for (final Player player : participants) {
      this.games.remove(player);
      final Audience kicked = this.audiences.player(player);
      kicked.sendMessage(kickedMessage);
    }

    audience.sendMessage(ownerMessage);
  }

  private boolean checkIfInNoGame(final Audience audience, final Pair<GameManager, Boolean> pair) {
    if (pair == null) {
      final Component message = Locale.GAME_INVALID_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean checkIfNotOwner(final Audience audience, final Pair<GameManager, Boolean> pair) {
    final boolean owner = pair.second();
    if (!owner) {
      final Component message = Locale.GAME_NOT_OWNER_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @CommandDescription("murder_run.command.game.invite.info")
  @Command(value = "murder game invite <invite>", requiredSender = Player.class)
  public void invitePlayer(final Player sender, final Player invite) {

    final Audience audience = this.audiences.player(sender);
    final Pair<GameManager, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data)
        || this.checkIfNotOwner(audience, data)
        || this.checkIfNotSamePlayer(audience, sender, invite)) {
      return;
    }

    final String senderDisplayName = sender.getDisplayName();
    final String inviteDisplayName = invite.getDisplayName();
    this.invites.computeIfAbsent(invite, k -> new HashSet<>());

    final Collection<Player> outgoing = this.invites.get(invite);
    outgoing.add(sender);

    final Component owner = Locale.GAME_OWNER_INVITE.build(inviteDisplayName);
    final Component player = Locale.GAME_PLAYER_INVITE.build(senderDisplayName);
    final Audience invited = this.audiences.player(invite);
    audience.sendMessage(owner);
    invited.sendMessage(player);
  }

  private boolean checkIfNotSamePlayer(
      final Audience audience, final Player sender, final Player invite) {
    if (sender == invite) {
      final Component message = Locale.GAME_INVITE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @CommandDescription("murder_run.command.game.join.info")
  @Command(value = "murder game join <owner>", requiredSender = Player.class)
  public void joinGame(final Player sender, final Player owner) {

    final Audience audience = this.audiences.player(sender);
    final Pair<GameManager, Boolean> data = this.games.get(sender);
    if (this.checkIfAlreadyInGame(audience, data)
        || this.checkIfNotInvited(audience, sender, owner)) {
      return;
    }

    final Collection<Player> invitations = this.invites.get(sender);
    final Pair<GameManager, Boolean> ownerData = this.games.get(owner);

    final GameManager manager = ownerData.first();
    manager.addParticipantToLobby(sender);
    invitations.remove(sender);

    final Collection<Player> participants = manager.getParticipants();
    final String name = sender.getDisplayName();
    final Component message = Locale.GAME_JOIN.build(name);
    for (final Player player : participants) {
      final Audience member = this.audiences.player(player);
      member.sendMessage(message);
    }
  }

  private boolean checkIfAlreadyInGame(
      final Audience audience, final Pair<GameManager, Boolean> data) {
    if (data != null) {
      final Component message = Locale.GAME_JOIN_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  private boolean checkIfNotInvited(
      final Audience audience, final Player sender, final Player owner) {
    final Collection<Player> invitations = this.invites.get(sender);
    if (invitations == null || !invitations.contains(owner)) {
      final Component message = Locale.GAME_INVALID_INVITE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @CommandDescription("murder_run.command.game.list.info")
  @Command(value = "murder game list", requiredSender = Player.class)
  public void listPlayers(final Player sender) {

    final Audience audience = this.audiences.player(sender);
    final Pair<GameManager, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data)) {
      return;
    }

    final GameManager manager = data.first();
    final List<String> names = this.constructPlayerList(manager);
    final Component message = Locale.GAME_LIST.build(names);
    audience.sendMessage(message);
  }

  private List<String> constructPlayerList(final GameManager manager) {
    final Collection<Player> participants = manager.getParticipants();
    final Collection<Player> murderers = manager.getMurderers();
    final List<String> names = new ArrayList<>();
    for (final Player player : participants) {
      String name = player.getDisplayName();
      name += murderers.contains(player) ? " (Killer)" : "";
      names.add(name);
    }
    return names;
  }

  @CommandDescription("murder_run.command.game.kick.info")
  @Command(value = "murder game kick <kick>", requiredSender = Player.class)
  public void kickPlayer(final Player sender, final Player kick) {

    final Audience audience = this.audiences.player(sender);
    final Pair<GameManager, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data) || this.checkIfNotOwner(audience, data)) {
      return;
    }

    final GameManager manager = data.first();
    manager.removeParticipantFromLobby(kick);
    this.games.remove(kick);

    final String name = kick.getDisplayName();
    final Audience player = this.audiences.player(kick);
    final Component ownerMessage = Locale.GAME_OWNER_KICK.build(name);
    final Component kickedMessage = Locale.GAME_PLAYER_KICK.build();
    audience.sendMessage(ownerMessage);
    player.sendMessage(kickedMessage);
  }

  @CommandDescription("murder_run.command.game.leave.info")
  @Command(value = "murder game leave", requiredSender = Player.class)
  public void leaveGame(final Player sender) {

    final Audience audience = this.audiences.player(sender);
    final Pair<GameManager, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data) || this.checkIfOwnerOfCurrentGame(audience, data)) {
      return;
    }

    final GameManager manager = data.first();
    manager.removeParticipantFromLobby(sender);
    this.games.remove(sender);

    final Component message = Locale.GAME_LEFT.build();
    audience.sendMessage(message);
  }

  private boolean checkIfOwnerOfCurrentGame(
      final Audience audience, final Pair<GameManager, Boolean> data) {
    final boolean owner = data.second();
    if (owner) {
      final Component message = Locale.GAME_LEAVE_ERROR.build();
      audience.sendMessage(message);
      return true;
    }
    return false;
  }

  @CommandDescription("murder_run.command.game.set.murderer.info")
  @Command(value = "murder game set murderer <murderer>", requiredSender = Player.class)
  public void setMurderer(final Player sender, final Player murderer) {

    final Audience audience = this.audiences.player(sender);
    final Pair<GameManager, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data) || this.checkIfNotOwner(audience, data)) {
      return;
    }

    final String name = murderer.getDisplayName();
    final GameManager manager = data.first();
    manager.setPlayerToMurderer(murderer);

    final Component message = Locale.GAME_SET_MURDERER.build(name);
    audience.sendMessage(message);
  }

  @CommandDescription("murder_run.command.game.set.innocent.info")
  @Command(value = "murder game set innocent <innocent>", requiredSender = Player.class)
  public void setInnocent(final Player sender, final Player innocent) {

    final Audience audience = this.audiences.player(sender);
    final Pair<GameManager, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data) || this.checkIfNotOwner(audience, data)) {
      return;
    }

    final String name = innocent.getDisplayName();
    final GameManager manager = data.first();
    manager.setPlayerToInnocent(innocent);

    final Component message = Locale.GAME_SET_INNOCENT.build(name);
    audience.sendMessage(message);
  }

  @CommandDescription("murder_run.command.game.set.car_part_count.info")
  @Command(value = "murder game set car-part-count <count>", requiredSender = Player.class)
  public void setCarPartCount(final Player sender, final int count) {

    final Audience audience = this.audiences.player(sender);
    final Pair<GameManager, Boolean> data = this.games.get(sender);
    if (this.checkIfInNoGame(audience, data) || this.checkIfNotOwner(audience, data)) {
      return;
    }

    final GameManager manager = data.first();
    final GameSettings settings = manager.getSettings();
    settings.setCarPartCount(count);

    final Component message = Locale.GAME_SET_CAR_PART_COUNT.build(count);
    audience.sendMessage(message);
  }

  @Suggestions("arena-suggestions")
  public List<String> arenaSuggestions(
      final CommandContext<CommandSender> context, final String input) {
    final ArenaManager manager = this.plugin.getArenaManager();
    return new ArrayList<>(manager.getArenaNames());
  }

  @Suggestions("lobby-suggestions")
  public List<String> lobbySuggestions(
      final CommandContext<CommandSender> context, final String input) {
    final LobbyManager manager = this.plugin.getLobbyManager();
    return new ArrayList<>(manager.getLobbyNames());
  }
}
