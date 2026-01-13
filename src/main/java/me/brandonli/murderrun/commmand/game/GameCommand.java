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
package me.brandonli.murderrun.commmand.game;

import static java.util.Objects.requireNonNull;

import java.util.*;
import java.util.function.Consumer;
import me.brandonli.murderrun.MurderRun;
import me.brandonli.murderrun.commmand.AnnotationCommandFeature;
import me.brandonli.murderrun.game.GameMode;
import me.brandonli.murderrun.game.arena.ArenaManager;
import me.brandonli.murderrun.game.extension.parties.PartiesManager;
import me.brandonli.murderrun.game.lobby.GameManager;
import me.brandonli.murderrun.game.lobby.LobbyManager;
import me.brandonli.murderrun.game.lobby.PreGameManager;
import me.brandonli.murderrun.game.lobby.PreGamePlayerManager;
import me.brandonli.murderrun.gui.game.PlayerListGui;
import me.brandonli.murderrun.locale.AudienceProvider;
import me.brandonli.murderrun.locale.Message;
import me.brandonli.murderrun.utils.ComponentUtils;
import me.brandonli.murderrun.utils.StreamUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

public final class GameCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;
  private GameInputSanitizer sanitizer;
  private InviteManager invites;

  @Override
  public void registerFeature(final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceProvider handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.sanitizer = new GameInputSanitizer(this);
    this.invites = new InviteManager();
    this.plugin = plugin;
  }

  @Permission("murderrun.command.game.party")
  @CommandDescription("murderrun.command.game.party.info")
  @Command(value = "murder game party <arenaName> <lobbyName>", requiredSender = Player.class)
  @SuppressWarnings("all") // checker
  public void startPartyGame(
    final Player player,
    @Argument(suggestions = "arena-suggestions") @Quoted final String arenaName,
    @Argument(suggestions = "lobby-suggestions") @Quoted final String lobbyName
  ) {
    final Audience audience = this.audiences.sender(player);

    if (
      this.sanitizer.checkIfPartyCapabilityDisabled(player, audience) ||
      this.sanitizer.checkIfPartyInvalid(player, audience) ||
      this.sanitizer.checkIfPartyNotLeader(player, audience)
    ) {
      return;
    }

    final PartiesManager manager = this.plugin.getPartiesManager();
    final UUID uuid = player.getUniqueId();
    final Collection<UUID> members = manager.getPartyMembers(player);
    final UUID id = manager.getPartyId(player);
    final int min = 2;
    final int max = members.size();
    final boolean quickJoinable = false;
    final String command = "murder game create %s %s %s %s %s %s".formatted(arenaName, lobbyName, id, min, max, quickJoinable);
    player.performCommand(command);

    final String joinCommand = "murder game join %s".formatted(id);
    final Consumer<Player> consumer = player1 -> {
      final String name = player1.getName();
      final String inviteCommand = "murder game invite %s".formatted(name);
      player.performCommand(inviteCommand);
      player1.performCommand(joinCommand);
    };

    members
      .stream()
      .map(manager::getBukkitUuid)
      .filter(StreamUtils.notEquals(uuid))
      .map(Bukkit::getPlayer)
      .filter(Objects::nonNull)
      .forEach(consumer);
  }

  @Permission("murderrun.command.game.start")
  @CommandDescription("murderrun.command.game.start.info")
  @Command(value = "murder game start", requiredSender = CommandSender.class)
  public void startGame(final CommandSender sender) {
    final Audience audience = this.audiences.sender(sender);
    final GameManager manager = this.plugin.getGameManager();
    final PreGameManager data = manager.getGame(sender);
    if (
      this.sanitizer.checkIfInNoGame(audience, data) ||
      this.sanitizer.checkIfNotOwner(sender, audience, requireNonNull(data)) ||
      this.sanitizer.checkIfGameAlreadyStarted(audience, data) ||
      this.sanitizer.checkIfNotEnoughPlayers(audience, data)
    ) {
      return;
    }
    data.startGame();

    final Component message = Message.GAME_START.build();
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.game.create")
  @CommandDescription("murderrun.command.game.create.info")
  @Command(
    value = "murder game create <arenaName> <lobbyName> <id> <mode> <min> <max> <quickJoinable>",
    requiredSender = CommandSender.class
  )
  public void createGame(
    final CommandSender sender,
    @Argument(suggestions = "arena-suggestions") @Quoted final String arenaName,
    @Argument(suggestions = "lobby-suggestions") @Quoted final String lobbyName,
    @Quoted final String id,
    final GameMode mode,
    final int min,
    final int max,
    final boolean quickJoinable
  ) {
    final Audience audience = this.audiences.sender(sender);
    final GameManager manager = this.plugin.getGameManager();
    final PreGameManager temp = manager.getGameAsParticipant(sender);
    if (
      this.sanitizer.checkIfAlreadyInGame(audience, temp) ||
      this.sanitizer.checkIfArenaValid(audience, arenaName) ||
      this.sanitizer.checkIfLobbyValid(audience, lobbyName) ||
      this.sanitizer.checkIfGameIdExists(audience, id) ||
      this.sanitizer.checkIfInvalidPlayerCounts(audience, min, max)
    ) {
      return;
    }
    manager
      .createGame(sender, id, mode, arenaName, lobbyName, min, max, quickJoinable)
      .thenRun(() -> audience.sendMessage(Message.GAME_CREATED.build()));
  }

  @Permission("murderrun.command.game.cancel")
  @CommandDescription("murderrun.command.game.cancel.info")
  @Command(value = "murder game cancel", requiredSender = Player.class)
  public void cancelGame(final Player sender) {
    final Audience audience = this.audiences.player(sender);
    final GameManager manager = this.plugin.getGameManager();
    final PreGameManager data = manager.getGame(sender);
    if (this.sanitizer.checkIfInNoGame(audience, data) || this.sanitizer.checkIfNotOwner(sender, audience, requireNonNull(data))) {
      return;
    }

    final String id = data.getId();
    manager.removeGame(id);

    final PreGamePlayerManager playerManager = data.getPlayerManager();
    final Collection<Player> participants = playerManager.getParticipants();
    final Component ownerMessage = Message.GAME_CANCEL.build();
    final Component kickedMessage = Message.GAME_PLAYER_KICK.build();
    for (final Player player : participants) {
      final Audience kicked = this.audiences.player(player);
      kicked.sendMessage(kickedMessage);
    }
    audience.sendMessage(ownerMessage);
  }

  @Permission("murderrun.command.game.invite")
  @CommandDescription("murderrun.command.game.invite.info")
  @Command(value = "murder game invite <invite>", requiredSender = Player.class)
  public void invitePlayer(final Player sender, final Player invite) {
    final Audience audience = this.audiences.player(sender);
    final GameManager manager = this.plugin.getGameManager();
    final PreGameManager data = manager.getGame(sender);
    if (
      this.sanitizer.checkIfInNoGame(audience, data) ||
      this.sanitizer.checkIfNotOwner(sender, audience, requireNonNull(data)) ||
      this.sanitizer.checkIfNotSamePlayer(audience, sender, invite) ||
      this.sanitizer.checkIfInvitedAlreadyInGame(audience, invite, data)
    ) {
      return;
    }
    this.invites.invitePlayer(sender, invite);

    final PreGameManager target = requireNonNull(manager.getGame(sender));
    final String id = target.getId();
    final Component component = invite.displayName();
    final String inviteDisplayName = ComponentUtils.serializeComponentToLegacyString(component);
    final Component owner = Message.GAME_OWNER_INVITE.build(inviteDisplayName);
    final Component player = Message.GAME_PLAYER_INVITE.build(id);
    final Audience invited = this.audiences.player(invite);
    audience.sendMessage(owner);
    invited.sendMessage(player);
  }

  @Permission("murderrun.command.game.player.join")
  @CommandDescription("murderrun.command.game.join.info")
  @Command(value = "murder game join <id>", requiredSender = Player.class)
  public void joinGame(final Player sender, @Quoted final String id) {
    final Audience audience = this.audiences.player(sender);
    final GameManager manager = this.plugin.getGameManager();
    final PreGameManager temp = manager.getGame(sender);
    if (this.sanitizer.checkIfAlreadyInGame(audience, temp) || this.sanitizer.checkIfNotInvited(audience, sender, id)) {
      return;
    }

    final PreGameManager data = requireNonNull(manager.getGame(id));
    if (this.sanitizer.checkIfGameFull(sender, audience, manager, data)) {
      return;
    }

    final PreGamePlayerManager playerManager = data.getPlayerManager();
    final CommandSender leader = playerManager.getLeader();
    this.invites.removeInvite(leader, sender);
    this.sendJoinMessage(sender, data);
  }

  private void sendJoinMessage(final Player sender, final PreGameManager data) {
    final PreGamePlayerManager playerManager = data.getPlayerManager();
    final Collection<Player> participants = playerManager.getParticipants();
    final Component component = sender.displayName();
    final String name = ComponentUtils.serializeComponentToLegacyString(component);
    final Component message = Message.GAME_JOIN.build(name);
    for (final Player player : participants) {
      final Audience member = this.audiences.player(player);
      member.sendMessage(message);
    }
  }

  @Permission("murderrun.command.game.list")
  @CommandDescription("murderrun.command.game.list.info")
  @Command(value = "murder game list", requiredSender = Player.class)
  public void listPlayers(final Player sender) {
    final Audience audience = this.audiences.player(sender);
    final GameManager manager = this.plugin.getGameManager();
    final PreGameManager data = manager.getGame(sender);
    if (this.sanitizer.checkIfInNoGame(audience, data)) {
      return;
    }

    final List<String> names = this.constructPlayerList(requireNonNull(data));
    final Component message = Message.GAME_LIST.build(names);
    audience.sendMessage(message);
  }

  private List<String> constructPlayerList(final PreGameManager manager) {
    final PreGamePlayerManager playerManager = manager.getPlayerManager();
    final Collection<Player> participants = playerManager.getParticipants();
    final Collection<Player> murderers = playerManager.getMurderers();
    final List<String> names = new ArrayList<>();
    for (final Player player : participants) {
      final Component component = player.displayName();
      String name = ComponentUtils.serializeComponentToLegacyString(component);
      name += murderers.contains(player) ? " (Killer)" : "";
      names.add(name);
    }
    return names;
  }

  @Permission("murderrun.command.game.kick")
  @CommandDescription("murderrun.command.game.kick.info")
  @Command(value = "murder game kick <kick>", requiredSender = Player.class)
  public void kickPlayer(final Player sender, final Player kick) {
    final Audience audience = this.audiences.player(sender);
    final GameManager manager = this.plugin.getGameManager();
    final PreGameManager data = manager.getGame(sender);
    if (
      this.sanitizer.checkIfInNoGame(audience, data) ||
      this.sanitizer.checkIfNotOwner(sender, audience, requireNonNull(data)) ||
      this.sanitizer.checkIfOwnerOfCurrentGame(sender, audience, data)
    ) {
      return;
    }
    manager.leaveGame(kick);
    this.invites.removeInvite(sender, kick);

    final Component component = kick.displayName();
    final String name = ComponentUtils.serializeComponentToLegacyString(component);
    final Audience player = this.audiences.player(kick);
    final Component ownerMessage = Message.GAME_OWNER_KICK.build(name);
    final Component kickedMessage = Message.GAME_PLAYER_KICK.build();
    audience.sendMessage(ownerMessage);
    player.sendMessage(kickedMessage);
  }

  @Permission("murderrun.command.game.leave")
  @CommandDescription("murderrun.command.game.leave.info")
  @Command(value = "murder game leave", requiredSender = Player.class)
  public void leaveGame(final Player sender) {
    final Audience audience = this.audiences.player(sender);
    final GameManager manager = this.plugin.getGameManager();
    final PreGameManager data = manager.getGame(sender);
    if (
      this.sanitizer.checkIfInNoGame(audience, data) || this.sanitizer.checkIfOwnerOfCurrentGame(sender, audience, requireNonNull(data))
    ) {
      return;
    }
    manager.leaveGame(sender);

    final Component message = Message.GAME_LEFT.build();
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.game.set.killer")
  @CommandDescription("murderrun.command.game.set.killer.info")
  @Command(value = "murder game set murderer <murderer>", requiredSender = Player.class)
  public void setMurderer(final Player sender, final Player murderer) {
    final Audience audience = this.audiences.player(sender);
    final GameManager manager = this.plugin.getGameManager();
    final PreGameManager data = manager.getGame(sender);
    if (
      this.sanitizer.checkIfInNoGame(audience, data) ||
      this.sanitizer.checkIfNotOwner(sender, audience, requireNonNull(data)) ||
      this.sanitizer.checkIfGameHasStarted(audience, data)
    ) {
      return;
    }

    final Component component = murderer.displayName();
    final String name = ComponentUtils.serializeComponentToLegacyString(component);
    final PreGamePlayerManager playerManager = data.getPlayerManager();
    playerManager.setPlayerToMurderer(murderer);

    final Component message = Message.GAME_SET_MURDERER.build(name);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.game.set.survivor")
  @CommandDescription("murderrun.command.game.set.survivor.info")
  @Command(value = "murder game set innocent <innocent>", requiredSender = Player.class)
  public void setInnocent(final Player sender, final Player innocent) {
    final Audience audience = this.audiences.player(sender);
    final GameManager manager = this.plugin.getGameManager();
    final PreGameManager data = manager.getGame(sender);
    if (
      this.sanitizer.checkIfInNoGame(audience, data) ||
      this.sanitizer.checkIfNotOwner(sender, audience, requireNonNull(data)) ||
      this.sanitizer.checkIfGameHasStarted(audience, data)
    ) {
      return;
    }

    final Component component = innocent.displayName();
    final String name = ComponentUtils.serializeComponentToLegacyString(component);
    final PreGamePlayerManager playerManager = data.getPlayerManager();
    playerManager.setPlayerToInnocent(innocent);

    final Component message = Message.GAME_SET_INNOCENT.build(name);
    audience.sendMessage(message);
  }

  @Permission("murderrun.command.game.quick-join")
  @CommandDescription("murderrun.command.game.join.quick.info")
  @Command(value = "murder game quick-join", requiredSender = Player.class)
  public void quickJoinGame(final Player sender) {
    final Audience audience = this.audiences.player(sender);
    final GameManager manager = this.plugin.getGameManager();
    final PreGameManager temp = manager.getGame(sender);
    if (this.sanitizer.checkIfAlreadyInGame(audience, temp)) {
      return;
    }

    this.sanitizer.checkIfNoQuickJoinableGame(sender, manager).thenAccept(value -> {
        if (value) {
          return;
        }
        final PreGameManager data = requireNonNull(manager.getGame(sender));
        final PreGamePlayerManager playerManager = data.getPlayerManager();
        final CommandSender leader = playerManager.getLeader();
        this.invites.removeInvite(leader, sender);
        this.sendJoinMessage(sender, (data));
      });
  }

  @Permission("murderrun.command.game.gui")
  @CommandDescription("murderrun.command.game.gui.info")
  @Command(value = "murder game gui", requiredSender = Player.class)
  public void openGameGui(final Player sender) {
    final GameManager manager = this.plugin.getGameManager();
    final PlayerListGui gui = new PlayerListGui(sender, manager);
    gui.update();
    gui.open(sender);
  }

  @Suggestions("arena-suggestions")
  public List<String> arenaSuggestions(final CommandContext<CommandSender> context, final String input) {
    final ArenaManager manager = this.plugin.getArenaManager();
    return new ArrayList<>(manager.getArenaNames());
  }

  @Suggestions("lobby-suggestions")
  public List<String> lobbySuggestions(final CommandContext<CommandSender> context, final String input) {
    final LobbyManager manager = this.plugin.getLobbyManager();
    return new ArrayList<>(manager.getLobbyNames());
  }

  public MurderRun getPlugin() {
    return this.plugin;
  }

  public BukkitAudiences getAudiences() {
    return this.audiences;
  }

  public GameInputSanitizer getSanitizer() {
    return this.sanitizer;
  }

  public InviteManager getInviteManager() {
    return this.invites;
  }
}
