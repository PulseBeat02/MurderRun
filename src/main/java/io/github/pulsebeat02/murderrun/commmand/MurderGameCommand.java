package io.github.pulsebeat02.murderrun.commmand;

import io.github.pulsebeat02.murderrun.MurderRun;
import io.github.pulsebeat02.murderrun.arena.MurderArena;
import io.github.pulsebeat02.murderrun.arena.MurderArenaManager;
import io.github.pulsebeat02.murderrun.lobby.MurderLobby;
import io.github.pulsebeat02.murderrun.lobby.MurderLobbyManager;
import io.github.pulsebeat02.murderrun.locale.AudienceHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotation.specifier.Quoted;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.stream.Stream;

public final class MurderGameCommand implements AnnotationCommandFeature {

  private MurderRun plugin;
  private BukkitAudiences audiences;
  private MurderArena arena;
  private MurderLobby lobby;

  @Override
  public void registerFeature(
      final MurderRun plugin, final AnnotationParser<CommandSender> parser) {
    final AudienceHandler handler = plugin.getAudience();
    this.audiences = handler.retrieve();
    this.plugin = plugin;
  }

  @CommandDescription(
      "Creates a game with the first argument being arena, second argument being lobby")
  @Command("murder game create <string> <string>")
  public void createGame(
      final CommandSender sender,
      @Argument(suggestions = "arena-suggestions") @Quoted final String arena,
      @Argument(suggestions = "lobby-suggestions") @Quoted final String lobby) {}

  @CommandDescription("Cancels the game, resetting the map and players")
  @Command("murder game cancel")
  public void cancelGame(final CommandSender sender) {}

  @CommandDescription("Sends an invite to a player to join your game lobby")
  @Command("murder game invite <entity>")
  public void invitePlayer(final CommandSender sender) {}

  @CommandDescription("Kicks a player from your game lobby")
  @Command("murder game kick <entity>")
  public void kickPlayer(final CommandSender sender) {}

  @CommandDescription("Leaves the current game")
  @Command("murder game leave")
  public void leaveGame(final CommandSender sender) {}

  @CommandDescription("Sets a player to be murderer")
  @Command("murder game set <entity> murderer")
  public void setMurderer(final CommandSender sender) {}

  @CommandDescription("Sets a player to be innocent")
  @Command("murder game set <entity> innocent")
  public void setInnocent(final CommandSender sender) {}

  @CommandDescription("Sets the car part count")
  @Command("murder game set car-par-count <int>")
  public void setCarPartCount(final CommandSender sender) {}

  @Suggestions("arena-suggestions")
  public Stream<String> arenaSuggestions(
      final CommandContext<CommandSender> context, final String input) {
    final MurderArenaManager manager = this.plugin.getArenaManager();
    return manager.getArenas().keySet().stream();
  }

  @Suggestions("lobby-suggestions")
  public Stream<String> lobbySuggestions(
      final CommandContext<CommandSender> context, final String input) {
    final MurderLobbyManager manager = this.plugin.getLobbyManager();
    return manager.getLobbies().keySet().stream();
  }
}
